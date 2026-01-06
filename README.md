# HaToKuSe: Hata-Toleranslı Kuyruk Servisi

## Dağıtık Mesaj Depolama Sistemi - Geliştirme Dokümantasyonu

**Geliştiriciler:** Hiranur Doğan(23060505), Ebubekir Yılmaz(23060561), Zeynep Sude Yüksel(23060563)  
**Tarih:** Ocak 2026  
**Versiyon:** 1.0-SNAPSHOT  
**Dil:** Java 11+ | gRPC 1.58.0 | Protocol Buffers 3.24.3

---

## İçindekiler

1. [Proje Tanımı ve Mimari](#1-proje-tanımı-ve-mimari)
2. [Adım Adım Geliştirme Süreci](#2-adım-adım-geliştirme-süreci)
3. [Teknik Özellikler Tablosu](#3-teknik-özellikler-tablosu)
4. [Çalıştırma Rehberi](#4-çalıştırma-rehberi)
5. [Test Senaryoları ve Doğrulama](#5-test-senaryoları-ve-doğrulama)
6. [Sonuç ve Değerlendirme](#6-sonuç-ve-değerlendirme)

---

## 1. Proje Tanımı ve Mimari

### 1.1 HaToKuSe Protokolü Nedir?

**HaToKuSe** (Hata-Toleranslı Kuyruk Servisi), dağıtık sistemlerde veri kaybını önlemek için tasarlanmış bir mesaj depolama protokolüdür. Protokolün temel felsefesi şudur:

> *"Bir veri parçası, en az T (tolerance) farklı düğümde saklanmadıkça, sistem tarafından kabul edilmiş sayılmaz."*

### 1.2 Neden HTTP Değil de Socket-Tabanlı İlkel Protokol?

Bu projede standart HTTP/REST yerine düşük seviyeli TCP socket iletişimi tercih edildi. Bunun teknik gerekçeleri:

| Kriter | HTTP/REST | Socket-Tabanlı HaToKuSe |
|--------|-----------|-------------------------|
| **Overhead** | HTTP header'ları, JSON parsing | Sadece `SET key value` veya `GET key` |
| **Latency** | HTTP handshake + TLS (varsa) | Doğrudan TCP bağlantısı |
| **Protokol Kontrolü** | Framework'e bağımlı | Byte seviyesinde tam kontrol |
| **Telnet Uyumluluğu** | Yok | Var (debug için kritik) |
| **Öğrenme Değeri** | Soyutlanmış | Network stack'in gerçek işleyişi |

**Örnek HaToKuSe Komutu:**
```
SET msg_1001 Bu bir test mesajıdır
GET msg_1001
```

Bu basitlik, sistemin herhangi bir Telnet istemcisiyle test edilebilmesini sağlar:
```bash
telnet localhost 6666
```

### 1.3 Lider-Üye (Leader-Follower) Mimarisi

Sistem, klasik **Leader-Follower** dağıtık mimari kalıbını uygular:

```
                    ┌─────────────────────────────────────────────────────────┐
                    │                      CLIENT(s)                          │
                    │              (Telnet / TCP Socket)                      │
                    └──────────────────────┬──────────────────────────────────┘
                                           │
                                           │ TCP Port 6666
                                           │ HaToKuSe Protocol
                                           ▼
                    ┌─────────────────────────────────────────────────────────┐
                    │                       LEADER                            │
                    │  ┌─────────────────────────────────────────────────┐    │
                    │  │ • ConfigManager (tolerance.conf okuma)          │    │
                    │  │ • MemberManager (üye kayıt/yönetim)             │    │
                    │  │ • LeaderServer  (client TCP handler)            │    │
                    │  │ • LeaderGrpcServer (member gRPC handler)        │    │
                    │  └─────────────────────────────────────────────────┘    │
                    │                                                         │
                    │  TCP:6666 (Client) │ gRPC:9001 (Member)                 │
                    └──────────────────────┬──────────────────────────────────┘
                                           │
                    ┌──────────────────────┼──────────────────────┐
                    │                      │                      │
                    ▼                      ▼                      ▼
          ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
          │    MEMBER_1     │    │    MEMBER_2     │    │    MEMBER_N     │
          │  gRPC:9100      │    │  gRPC:9101      │    │  gRPC:910N      │
          │  ┌───────────┐  │    │  ┌───────────┐  │    │  ┌───────────┐  │
          │  │DiskManager│  │    │  │DiskManager│  │    │  │DiskManager│  │
          │  │ Zero-Copy │  │    │  │ Buffered  │  │    │  │ Zero-Copy │  │
          │  └───────────┘  │    │  └───────────┘  │    │  └───────────┘  │
          │       │         │    │       │         │    │       │         │
          │       ▼         │    │       ▼         │    │       ▼         │
          │  data/member_1/ │    │  data/member_2/ │    │  data/member_N/ │
          └─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 1.4 Veri Akışı (SET Komutu)

```
Client ──SET msg_1 hello──► Leader
                              │
                              ▼
                    ┌─────────────────────┐
                    │ 1. tolerance.conf   │
                    │    değerini oku (T) │
                    └─────────────────────┘
                              │
                              ▼
                    ┌─────────────────────┐
                    │ 2. Round-Robin ile  │
                    │    T üye seç        │
                    └─────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
           ┌─────────────┐     ┌─────────────┐
           │ Member_1    │     │ Member_2    │
           │ StoreMessage│     │ StoreMessage│
           │ (Parallel)  │     │ (Parallel)  │
           └─────────────┘     └─────────────┘
                    │                   │
                    └─────────┬─────────┘
                              ▼
                    ┌─────────────────────┐
                    │ 3. T başarılı yanıt │
                    │    alındı mı?       │
                    └─────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
                  Evet                Hayır
                   │                   │
                   ▼                   ▼
           "OK" ◄──┘           "ERROR" ◄──┘
```

---

## 2. Adım Adım Geliştirme Süreci

### Aşama 1: Veri Yapılarının Tanımlanması (Protocol Buffers)

İlk adım olarak, Leader-Member arasındaki iletişimi tanımlayan `.proto` dosyası oluşturuldu. gRPC tercih edilmesinin nedenleri:

- **Tip güvenliği:** Protobuf, derleme zamanında şema kontrolü sağlar
- **Performans:** Binary serialization, JSON'dan ~10x daha hızlı
- **Kod üretimi:** Stub'lar otomatik oluşturulur

**`message_service.proto` Dosyası:**

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_package = "com.distributed.grpc";

package messageservice;

service MemberService {
    rpc Register(RegisterRequest) returns (RegisterResponse);
    rpc Unregister(UnregisterRequest) returns (UnregisterResponse);
    rpc StoreMessage(StoreMessageRequest) returns (StoreMessageResponse);
    rpc RetrieveMessage(RetrieveMessageRequest) returns (RetrieveMessageResponse);
    rpc Heartbeat(HeartbeatRequest) returns (HeartbeatResponse);
}
```

**Tasarım Kararları:**

| RPC Metodu | Amaç | Çağıran |
|------------|------|---------|
| `Register` | Üye sisteme katılır | Member → Leader |
| `Unregister` | Üye sistemden ayrılır | Member → Leader |
| `StoreMessage` | Mesaj depolama | Leader → Member |
| `RetrieveMessage` | Mesaj okuma | Leader → Member |
| `Heartbeat` | Canlılık kontrolü | Leader → Member |

### Aşama 2: HaToKuSe Protokolü (Client-Leader İletişimi)

Client ile Leader arasındaki iletişim, **metin tabanlı TCP socket** üzerinden gerçekleşir. Bu tasarım, Telnet ile doğrudan test imkanı sağlar.

**`ProtocolConstants.java` - Protokol Sabitleri:**

```java
public final class ProtocolConstants {
    public static final String CMD_SET = "SET";
    public static final String CMD_GET = "GET";
    public static final String RESPONSE_OK = "OK";
    public static final String RESPONSE_ERROR = "ERROR";
    public static final int DEFAULT_LEADER_CLIENT_PORT = 6666;
    public static final int DEFAULT_LEADER_GRPC_PORT = 9001;
    public static final int GRPC_TIMEOUT_MS = 5000;
    
}
```

**`LeaderServer.java` - Komut İşleme:**

```java
private String processCommand(String command) {
    String[] parts = command.split("\\s+", 3);
    String cmd = parts[0].toUpperCase();
    String messageId = parts[1];

    if (ProtocolConstants.CMD_SET.equals(cmd) && parts.length >= 3) {
        return handleSet(messageId, parts[2]);
    } else if (ProtocolConstants.CMD_GET.equals(cmd)) {
        return handleGet(messageId);
    }
    return ProtocolConstants.RESPONSE_ERROR;
}
```

**Protokol Formatı:**

```
┌─────────────────────────────────────────────────────────┐
│ SET <message_id> <message_content>                      │
│ Örnek: SET msg_1001 Bu bir test mesajıdır               │
├─────────────────────────────────────────────────────────┤
│ Yanıt: OK | ERROR                                       │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ GET <message_id>                                        │
│ Örnek: GET msg_1001                                     │
├─────────────────────────────────────────────────────────┤
│ Yanıt: <message_content> | ERROR                        │
└─────────────────────────────────────────────────────────┘
```

### Aşama 3: Lider ve Yük Dağıtımı (Round-Robin)

#### 3.1 Tolerans Yapılandırması

Sistem, `tolerance.conf` dosyasından tolerans değerini okur:

```properties
tolerance=3
```

**`ConfigManager.java`:**

```java
public int getTolerance() {
    try (BufferedReader reader = new BufferedReader(new FileReader(configPath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("tolerance=")) {
                return Integer.parseInt(line.substring("tolerance=".length()).trim());
            }
        }
    } catch (IOException | NumberFormatException e) {
        throw new RuntimeException("Config okunamadı: " + configPath);
    }
    throw new RuntimeException("tolerance değeri bulunamadı");
}
```

#### 3.2 Round-Robin Üye Seçimi

Her mesaj için farklı üyeler seçilerek yükün dengeli dağılması sağlanır:

**`MemberManager.java` - Round-Robin Algoritması:**

```java
public List<String> selectMembersForMessage(int count) {
    List<MemberInfo> aliveMembers = new ArrayList<>();
    for (MemberInfo m : members.values()) {
        if (m.isAlive()) aliveMembers.add(m);
    }
    if (aliveMembers.size() < count) return Collections.emptyList();

   
    aliveMembers.sort(Comparator.comparing(MemberInfo::getMemberId));
    
    List<String> selected = new ArrayList<>();
    int size = aliveMembers.size();
    long startIndex = roundRobinCounter.getAndIncrement() % size;

    for (int i = 0; i < count; i++) {
        selected.add(aliveMembers.get((int) ((startIndex + i) % size)).getMemberId());
    }
    return selected;
}
```

**Round-Robin Çalışma Örneği (Tolerance=2, 4 Üye):**

| Mesaj # | Counter | Seçilen Üyeler |
|---------|---------|----------------|
| msg_1 | 0 | member_1, member_2 |
| msg_2 | 1 | member_2, member_3 |
| msg_3 | 2 | member_3, member_4 |
| msg_4 | 3 | member_4, member_1 |
| msg_5 | 4 | member_1, member_2 |

Bu yaklaşım, mesajların **eşit dağılımını** garanti eder.

#### 3.3 Asenkron Paralel Mesaj Gönderimi

Mesajlar üyelere **eş zamanlı (paralel)** olarak gönderilir. Bu, sıralı gönderime göre T kat daha hızlıdır:

```java
public boolean storeMessageToMembersAsync(String messageId, String messageContent, 
                                          List<String> memberIds) {
    AtomicInteger successCount = new AtomicInteger(0);
    Set<String> successfulMembers = ConcurrentHashMap.newKeySet();
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    for (String memberId : memberIds) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
           
            if (response.getSuccess()) {
                successCount.incrementAndGet();
                successfulMembers.add(memberId);
            }
        }, asyncExecutor); 
        futures.add(future);
    }

   
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    return successCount.get() >= memberIds.size();
}
```

**Performans Karşılaştırması:**

| Yöntem | 1000 Mesaj (T=3) | Açıklama |
|--------|------------------|----------|
| Sıralı | ~15 saniye | Her mesaj için 3 ardışık RPC |
| Paralel | ~5 saniye | 3 RPC eş zamanlı |

### Aşama 4: Veri Saklama ve Performans (Disk I/O Stratejileri)

Bu aşama, projenin **en kritik teknik bölümüdür**. Üyeler mesajları diske kaydederken iki farklı strateji desteklenir:

#### 4.1 WriteStrategy Enum

```java
public enum WriteStrategy { 
    ZERO_COPY,   
    BUFFERED     
}
```

#### 4.2 Zero-Copy (FileChannel + Direct ByteBuffer)

**Zero-copy**, verinin kullanıcı alanı (user space) ve çekirdek alanı (kernel space) arasında gereksiz kopyalanmasını önleyen bir tekniktir.

```java
private boolean storeWithFileChannel(String messageId, String content) {
    try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw");
         FileChannel channel = raf.getChannel()) {
        
        byte[] data = content.getBytes();
        
        
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        buffer.flip(); 
        
        channel.truncate(0); 
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        channel.force(true); 

        return true;
    } catch (IOException e) {
        return false;
    }
}
```

**Bellek Düzeni:**

```
┌─────────────────────────────────────────────────────────────┐
│ GELENEKSEL I/O (Heap Buffer)                                │
├─────────────────────────────────────────────────────────────┤
│ User Space:  [JVM Heap] ──copy──► [Native Buffer]           │
│ Kernel Space: ──copy──► [Kernel Buffer] ──► [Disk]          │
│ Toplam Kopya: 2                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ ZERO-COPY (Direct ByteBuffer)                               │
├─────────────────────────────────────────────────────────────┤
│ User Space:  [Direct Buffer] ────────────────────┐          │
│ Kernel Space: ───────────────────────────────────┴─► [Disk] │
│ Toplam Kopya: 0-1 (OS optimizasyonuna bağlı)                │
└─────────────────────────────────────────────────────────────┘
```

**`channel.force(true)` Önemi:**

Bu çağrı, işletim sistemi önbelleğindeki verilerin fiziksel diske yazılmasını garanti eder (fsync). Güç kesintisi durumunda veri kaybını önler.

#### 4.3 Buffered I/O

Daha basit ama yine de optimize edilmiş bir yaklaşım:

```java
private boolean storeWithBufferedStream(String messageId, String content) {
    try (BufferedOutputStream bos = new BufferedOutputStream(
            new FileOutputStream(filePath.toFile()), 8192)) {  
        bos.write(content.getBytes());
        bos.flush();
        return true;
    } catch (IOException e) {
        return false;
    }
}
```

**Strateji Karşılaştırması:**

| Özellik | Zero-Copy | Buffered |
|---------|-----------|----------|
| Bellek Kullanımı | Off-heap (native) | On-heap (JVM) |
| CPU Overhead | Düşük | Orta |
| Küçük Dosyalar (<1KB) | Overhead yüksek | Verimli |
| Büyük Dosyalar (>1MB) | Çok verimli | Orta |
| GC Etkisi | Yok | Var |
| Kod Karmaşıklığı | Yüksek | Düşük |

#### 4.4 Strateji Seçimi

Üye başlatılırken `--disk-strategy` parametresi ile strateji seçilebilir:

```bash
# Zero-copy (varsayılan)
run_member.bat --id member_1 --disk-strategy ZERO_COPY

# Buffered
run_member.bat --id member_2 --disk-strategy BUFFERED
```

### Aşama 5: Hata Toleransı

#### 5.1 Üye Durumu Yönetimi

Her üyenin durumu `MemberInfo` sınıfında takip edilir:

```java
public class MemberInfo {
    private final String memberId;
    private final String host;
    private final int port;
    private volatile boolean alive;
    private final AtomicInteger messageCount;
    
}
```

#### 5.2 Üye Çökmesi Tespiti

Bir gRPC çağrısı başarısız olduğunda, üye "DEAD" olarak işaretlenir:

```java
try {
    StoreMessageResponse response = stub
        .withDeadlineAfter(GRPC_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .storeMessage(request);
} catch (StatusRuntimeException e) {
    markMemberDead(memberId);  
}
```

#### 5.3 Veri Kurtarma

GET komutu, mesajı tutan **canlı** bir üyeden okumaya çalışır:

```java
public String retrieveMessage(String messageId) {
    Set<String> holdingMembers = messageMetadata.get(messageId);
    if (holdingMembers == null) return null;

    for (String memberId : holdingMembers) {
        MemberInfo member = members.get(memberId);
       
        if (member == null || !member.isAlive()) continue;

        try {
            RetrieveMessageResponse response = stub.retrieveMessage(request);
            if (response.getFound()) return response.getMessageContent();
        } catch (StatusRuntimeException e) {
            markMemberDead(memberId);
           
        }
    }
    return null;  
}
```

**Senaryo: Tolerance=3, 5 Üye**

```
Başlangıç Durumu:
  msg_1001 → [member_1 ✓, member_2 ✓, member_3 ✓]

member_2 çöker:
  msg_1001 → [member_1 ✓, member_2 ✗, member_3 ✓]

GET msg_1001:
  1. member_1'e sor → BAŞARILI → içerik döner
  
member_1 de çöker:
  msg_1001 → [member_1 ✗, member_2 ✗, member_3 ✓]

GET msg_1001:
  1. member_1'e sor → TIMEOUT → atla
  2. member_3'e sor → BAŞARILI → içerik döner
```

---

## 3. Teknik Özellikler Tablosu

| Kategori | Teknoloji/Özellik | Detay |
|----------|-------------------|-------|
| **Dil** | Java | 11+ (LTS) |
| **Build** | Maven | 3.8+ (Wrapper dahil) |
| **RPC Framework** | gRPC | 1.58.0 |
| **Serialization** | Protocol Buffers | 3.24.3 |
| **Disk I/O** | Java NIO | FileChannel, ByteBuffer |
| **Concurrency** | java.util.concurrent | ConcurrentHashMap, CompletableFuture, ExecutorService |
| **Thread Safety** | Atomic Types | AtomicInteger, AtomicLong |
| **Client Protocol** | TCP Socket | Port 6666 (Telnet uyumlu) |
| **Inter-node Protocol** | gRPC | Port 9001 (Leader), 910x (Members) |
| **Max Message Size** | 16 MB | gRPC channel config |
| **RPC Timeout** | 5000 ms | Configurable |
| **Log Period** | 5000 ms | Status print interval |

---

## 4. Çalıştırma Rehberi

### 4.1 Ön Gereksinimler

- Java 11 veya üstü (JDK)
- Maven 3.8+ (veya dahili Maven Wrapper)
- Windows / Linux / macOS

### 4.2 Projeyi Derleme

```bash
# Maven Wrapper ile (önerilen)
.\mvnw.cmd clean package -DskipTests

# veya Maven yüklüyse
mvn clean package -DskipTests
```

Başarılı derleme sonrası `target/` dizininde:
- `leader.jar` - Lider sunucu
- `member.jar` - Üye sunucu
- `client.jar` - Test istemcisi

### 4.3 Tolerans Ayarı

`tolerance.conf` dosyasını düzenleyin:

```properties
tolerance=3
```

> **Not:** Tolerance değeri, en az kaç üyenin başlatılması gerektiğini belirler.

### 4.4 Sistemi Başlatma

#### Adım 1: Leader'ı Başlat

```bash
.\run_leader.bat
# veya
java -jar target\leader.jar --config tolerance.conf
```

**Parametreler:**
| Parametre | Varsayılan | Açıklama |
|-----------|------------|----------|
| `--client-port` | 6666 | Client TCP portu |
| `--grpc-port` | 9001 | Member gRPC portu |
| `--config` | tolerance.conf | Yapılandırma dosyası |

#### Adım 2: Member'ları Başlat

Her üye için ayrı terminal açın:

```bash
# Üye 1
.\run_member.bat --id member_1 --port 9100 --disk-strategy ZERO_COPY

# Üye 2
.\run_member.bat --id member_2 --port 9101 --disk-strategy BUFFERED

# Üye 3
.\run_member.bat --id member_3 --port 9102 --disk-strategy ZERO_COPY
```

**Parametreler:**
| Parametre | Varsayılan | Açıklama |
|-----------|------------|----------|
| `--id` | member_X | Benzersiz üye kimliği |
| `--port` | 9100 | gRPC dinleme portu |
| `--leader-host` | localhost | Leader adresi |
| `--leader-port` | 9001 | Leader gRPC portu |
| `--disk-strategy` | ZERO_COPY | ZERO_COPY veya BUFFERED |

#### Adım 3: Test (Telnet ile)

```bash
telnet localhost 6666

SET msg_1001 Merhaba Dünya
OK

GET msg_1001
Merhaba Dünya
```

### 4.5 Hazır Test Script'leri

```bash
# Test 1: Tolerance 2, 2 Üye, 1000 Mesaj
.\start_test1.bat

# Test 2: Tolerance 3, 6 Üye, 9000 Mesaj
.\start_test2.bat
```

---

## 5. Test Senaryoları ve Doğrulama

### Senaryo 1: Tolerance=2, 1000 Mesaj Dağılımı

**Yapılandırma:**
```properties
tolerance=2
```

**Üyeler:** member_1, member_2

**Beklenen Davranış:**
- Her mesaj 2 üyeye kopyalanır
- Round-Robin ile dağılım: [1,2], [2,1], [1,2]...
- Her üyede **500 mesaj** bulunur

**Test Komutu:**
```bash
# Client ile 1000 mesaj gönder
java -jar target\client.jar --count 1000 --prefix msg_
```

**Doğrulama:**
```powershell
# Her üyenin mesaj sayısını kontrol et
(Get-ChildItem data\member_1\*.txt).Count  # Beklenen: 1000
(Get-ChildItem data\member_2\*.txt).Count  # Beklenen: 1000
```

> **Not:** Her mesaj 2 üyeye gittiğinden, toplam dosya sayısı 2000'dir (1000 mesaj × 2 kopya).

### Senaryo 2: Tolerance=3, 9000 Mesaj (6 Üye)

**Yapılandırma:**
```properties
tolerance=3
```

**Üyeler:** member_1 ... member_6

**Dağılım Matematiği:**
- 9000 mesaj × 3 kopya = 27000 toplam dosya
- 27000 / 6 üye = **4500 dosya/üye**

**Round-Robin Dağılım Örüntüsü:**

| Mesaj | Seçilen Üyeler |
|-------|----------------|
| msg_1 | 1, 2, 3 |
| msg_2 | 2, 3, 4 |
| msg_3 | 3, 4, 5 |
| msg_4 | 4, 5, 6 |
| msg_5 | 5, 6, 1 |
| msg_6 | 6, 1, 2 |
| ... | ... |

**Doğrulama Script'i:**
```powershell
$total = 0
for ($i = 1; $i -le 6; $i++) {
    $count = (Get-ChildItem "data\member_$i\*.txt").Count
    Write-Host "member_$i: $count dosya"
    $total += $count
}
Write-Host "Toplam: $total (Beklenen: 27000)"
```

### Senaryo 3: Crash Test (Üye Çökmesi)

**Amaç:** Bir üye çöktüğünde verilerin hala erişilebilir olduğunu kanıtlamak.

**Adımlar:**

1. **Sistemi başlat** (tolerance=3, 3 üye)
2. **Mesaj gönder:**
   ```
   SET critical_data_001 Bu çok önemli bir veri
   OK
   ```
3. **Mesajın nerede saklandığını kontrol et:**
   ```powershell
   Get-ChildItem data\member_*\message_critical_data_001.txt
   ```
   Çıktı: 3 dosya (member_1, member_2, member_3)

4. **member_2'yi öldür** (terminal'i kapat veya `taskkill`)

5. **Veriyi oku:**
   ```
   GET critical_data_001
   Bu çok önemli bir veri    # BAŞARILI!
   ```

6. **member_1'i de öldür**

7. **Tekrar oku:**
   ```
   GET critical_data_001
   Bu çok önemli bir veri    # HALA BAŞARILI! (member_3'ten)
   ```

8. **member_3'ü de öldür**

9. **Tekrar oku:**
   ```
   GET critical_data_001
   ERROR    # Tüm kopyalar erişilemez
   ```

**Sonuç:** Tolerance=3 ile, 3 üyeden 2'si çökse bile veri erişilebilir kalır.

---

## 6. Sonuç ve Değerlendirme

### 6.1 Başarılan Hedefler

| Hedef | Durum | Açıklama |
|-------|-------|----------|
| ✅ HaToKuSe Protokolü | Tamamlandı | SET/GET komutları, Telnet uyumlu |
| ✅ gRPC İletişimi | Tamamlandı | Leader-Member arası Protobuf |
| ✅ Tolerans Yapılandırması | Tamamlandı | tolerance.conf ile dinamik |
| ✅ Round-Robin Dağıtım | Tamamlandı | Eşit yük dağılımı |
| ✅ Zero-Copy Disk I/O | Tamamlandı | FileChannel + Direct ByteBuffer |
| ✅ Buffered I/O | Tamamlandı | 8KB buffer ile |
| ✅ Asenkron gRPC | Tamamlandı | CompletableFuture ile paralel |
| ✅ Hata Toleransı | Tamamlandı | T-1 üye çökse bile veri korunur |
| ✅ n≤7 Desteği | Tamamlandı | Dinamik üye sayısı |

### 6.2 Kod Kalitesi Metrikleri

- **Toplam Satır:** ~1500 (test hariç)
- **Sınıf Sayısı:** 12
- **Thread-safe Yapılar:** ConcurrentHashMap, AtomicInteger, AtomicLong
- **Kaynak Yönetimi:** try-with-resources pattern

### 6.3 Potansiyel İyileştirmeler

1. **Leader Redundancy:** Şu an tek leader noktası (SPOF)
2. **Persistent Message Metadata:** Leader restart'ta metadata kaybı
3. **Compression:** Büyük mesajlar için gzip
4. **TLS:** Güvenli iletişim
5. **Monitoring:** Prometheus/Grafana entegrasyonu

---

## Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

---

*Son güncelleme: Ocak 2026*
