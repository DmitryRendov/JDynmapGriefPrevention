package jahan.khan.jdynmapgriefprevention;

import java.util.Comparator;

public class VersionComparator implements Comparator<Object> {
    public boolean equals(Object o1, Object o2) {
        return compare(o1, o2) == 0;
    }

    public int compare(Object o1, Object o2) {
        String version1 = (String) o1;
        String version2 = (String) o2;
        VersionTokenizer tokenizer1 = new VersionTokenizer(version1);
        VersionTokenizer tokenizer2 = new VersionTokenizer(version2);
        int number1 = 0;
        int number2 = 0;
        String suffix1 = "";
        String suffix2 = "";
        while (tokenizer1.MoveNext()) {
            if (!tokenizer2.MoveNext()) {
                do {
                    number1 = tokenizer1.getNumber();
                    suffix1 = tokenizer1.getSuffix();
                    if ((number1 != 0) || (suffix1.length() != 0)) {
                        return 1;
                    }

                } while (tokenizer1.MoveNext());

                return 0;
            }
            number1 = tokenizer1.getNumber();
            suffix1 = tokenizer1.getSuffix();
            number2 = tokenizer2.getNumber();
            suffix2 = tokenizer2.getSuffix();
            if (number1 < number2) {
                return -1;
            }
            if (number1 > number2) {
                return 1;
            }
            boolean empty1 = suffix1.length() == 0;
            boolean empty2 = suffix2.length() == 0;
            if ((!empty1) || (!empty2)) {
                if (empty1)
                    return 1;
                if (empty2) {
                    return -1;
                }
                int result = suffix1.compareTo(suffix2);
                if (result != 0)
                    return result;
            }
        }
        if (tokenizer2.MoveNext()) {
            do {
                number2 = tokenizer2.getNumber();
                suffix2 = tokenizer2.getSuffix();
                if ((number2 != 0) || (suffix2.length() != 0)) {
                    return -1;
                }

            } while (tokenizer2.MoveNext());

            return 0;
        }
        return 0;
    }
}
