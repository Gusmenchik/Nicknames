import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final AtomicInteger beautifulWordsLength3 = new AtomicInteger(0);
    private static final AtomicInteger beautifulWordsLength4 = new AtomicInteger(0);
    private static final AtomicInteger beautifulWordsLength5 = new AtomicInteger(0);

    public static void main(String[] args) {
        Random random = new Random();
        String[] texts = new String[100_000];

        // Генерация 100,000 коротких слов
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        // Запуск потоков для проверки каждого критерия
        Thread palindromeThread = new Thread(() -> countBeautifulWords(texts, 3, Main::isPalindrome));
        Thread singleLetterThread = new Thread(() -> countBeautifulWords(texts, 4, Main::isSingleLetter));
        Thread ascendingOrderThread = new Thread(() -> countBeautifulWords(texts, 5, Main::isAscendingOrder));

        palindromeThread.start();
        singleLetterThread.start();
        ascendingOrderThread.start();

        try {
            palindromeThread.join();
            singleLetterThread.join();
            ascendingOrderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Вывод результатов
        System.out.println("Красивых слов с длиной 3: " + beautifulWordsLength3.get() + " шт");
        System.out.println("Красивых слов с длиной 4: " + beautifulWordsLength4.get() + " шт");
        System.out.println("Красивых слов с длиной 5: " + beautifulWordsLength5.get() + " шт");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void countBeautifulWords(String[] texts, int length, WordChecker wordChecker) {
        for (String text : texts) {
            if (text.length() == length && wordChecker.check(text)) {
                switch (length) {
                    case 3:
                        beautifulWordsLength3.incrementAndGet();
                        break;
                    case 4:
                        beautifulWordsLength4.incrementAndGet();
                        break;
                    case 5:
                        beautifulWordsLength5.incrementAndGet();
                        break;
                }
            }
        }
    }

    public static boolean isPalindrome(String word) {
        return word.equals(new StringBuilder(word).reverse().toString());
    }

    public static boolean isSingleLetter(String word) {
        return word.replaceAll(String.valueOf(word.charAt(0)), "").isEmpty();
    }

    public static boolean isAscendingOrder(String word) {
        for (int i = 1; i < word.length(); i++) {
            if (word.charAt(i) < word.charAt(i - 1)) {
                return false;
            }
        }
        return true;
    }

    interface WordChecker {
        boolean check(String word);
    }
}
