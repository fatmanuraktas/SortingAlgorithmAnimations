import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

public class RadixSortPanel extends SortPanel {
    private static final long serialVersionUID = 1L;
    private int redColumn = -1;   // Kopyalama (Yazma) işlemi
    private int blueColumn = -1;  // Okuma/Sayma işlemi
    private int cyanColumn = -1;  // Yerleştirme (Output) aşaması
    private int greenColumn = -1; // Bitiş efekti sayacı

    public RadixSortPanel(String name, int sleepTime, int width, int height) {
        super(name, sleepTime, width, height);
    }

    @Override
    public void reset() {
        redColumn = -1;
        blueColumn = -1;
        cyanColumn = -1;
        greenColumn = -1;
    }

    @Override
    public void run() {
        try {
            // Başlangıç temizliği
            reset();
            
            int m = getMax();
            
            // --- SIRALAMA AŞAMASI ---
            // Her basamak için (1'ler, 10'lar...) counting sort uygula
            for (int exp = 1; m / exp > 0; exp *= 10) {
                countSort(exp);
                // Basamaklar arası bekleme (isteğe bağlı)
                Thread.sleep(sleepTime);
            }
            
            // --- BİTİŞ ANİMASYONU (YEŞİL TARAMA) ---
            // Sıralama bitti, diğer renkleri kapat
            redColumn = -1;
            blueColumn = -1;
            cyanColumn = -1;
            
            // Listeyi baştan sona tek tek yeşil yap
            for (int i = 0; i < list.length; i++) {
                greenColumn = i; // Yeşilin sınırı artıyor
                repaint();
                Thread.sleep(10); // Tarama hızı (sabit ve hızlı olsun)
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int getMax() {
        if (list.length == 0) return 0;
        int mx = list[0];
        for (int i = 1; i < list.length; i++)
            if (list[i] > mx)
                mx = list[i];
        return mx;
    }

    private void countSort(int exp) throws InterruptedException {
        int n = list.length;
        int output[] = new int[n];
        int count[] = new int[10];
        Arrays.fill(count, 0);

        // Her yeni basamak başında renkleri temizle
        redColumn = -1;
        cyanColumn = -1;
        blueColumn = -1;
        repaint();

        // 1. AŞAMA (MAVİ): Frekansları say (Scanning)
        for (int i = 0; i < n; i++) {
            blueColumn = i; 
            cyanColumn = -1; 
            repaint();
            Thread.sleep(sleepTime); 
            
            count[(list[i] / exp) % 10]++;
        }

        // Count dizisini kümülatif yap
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        // 2. AŞAMA (CYAN): Output dizisini oluştur
        // Burada görsel olarak çubukların yer değiştirmesini göremezsin 
        // çünkü "output" geçici dizisine yazıyoruz.
        // Ancak işlemin ilerleyişini (scanning) göstermek için Cyan kullanıyoruz.
        for (int i = n - 1; i >= 0; i--) {
            cyanColumn = i; // Tarama çizgisi
            blueColumn = -1;
            repaint();
            Thread.sleep(sleepTime);

            output[count[(list[i] / exp) % 10] - 1] = list[i];
            count[(list[i] / exp) % 10]--;
        }

        // 3. AŞAMA (KIRMIZI): Ana listeye kopyala (GÜNCELLEME BURADA GÖRÜLÜR)
        for (int i = 0; i < n; i++) {
            list[i] = output[i];
            
            redColumn = i;   // Kırmızı çizgi verinin değiştiğini gösterir
            cyanColumn = -1; 
            repaint();
            Thread.sleep(sleepTime);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int columnWidth = (getWidth() - 4 * BORDER_WIDTH) / size;
        int columnHeight = (getHeight() - 4 * BORDER_WIDTH) / size;

        for (int i = 0; i < list.length; i++) {
            
            // --- RENK MANTIĞI ---
            
            // 1. Bitiş Animasyonu (Yeşil Tarama)
            if (greenColumn != -1 && i <= greenColumn) {
                // Eğer bitiş animasyonu başladıysa ve sıra bu çubuktaysa YEŞİL yap
                g.setColor(Color.GREEN);
            } 
            // 2. Sıralama İşlemleri (Öncelik Sırasıyla)
            else {
                // Varsayılan Beyaz
                g.setColor(Color.WHITE);

                // Kırmızı: Değer kopyalanıyor (En önemli işlem)
                if (i == redColumn) {
                    g.setColor(Color.RED);
                }
                // Cyan: Output dizisine yerleştirme hesaplanıyor (Sondan başa tarama)
                else if (cyanColumn != -1 && i >= cyanColumn) {
                    g.setColor(Color.CYAN);
                }
                // Mavi: Okuma yapılıyor
                else if (i == blueColumn) {
                    g.setColor(Color.BLUE);
                }
            }

            // Çubuğu Çiz
            g.fillRect(2 * BORDER_WIDTH + columnWidth * i, getHeight() - list[i] * columnHeight - 2 * BORDER_WIDTH, columnWidth, list[i] * columnHeight);
            
            // Çerçeve (Siyah)
            g.setColor(Color.BLACK);
            g.drawRect(2 * BORDER_WIDTH + columnWidth * i, getHeight() - list[i] * columnHeight - 2 * BORDER_WIDTH, columnWidth, list[i] * columnHeight);
        }
    }
}