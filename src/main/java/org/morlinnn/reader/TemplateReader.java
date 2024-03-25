package org.morlinnn.reader;

import org.morlinnn.enums.DataType;
import org.morlinnn.reader.template.TemplateElement;
import org.morlinnn.reader.template.TemplateElementFactory;

import java.util.*;

public class TemplateReader {
    public static TemplateElement parse(String str) {
        if (!checkValid(str)) {
            System.out.println("无效的模板元素: " + str);
            return null;
        }

        String[] divided = divide(str);
        if (divided == null) {
            System.out.println("模板为空: " + str);
            return null;
        }

        String name = divided[0];
        List<String> field = divideFiled(divided[1], ',');

        TemplateElementFactory templateElementFactory =
                new TemplateElementFactory(name, isSelectTemplate(divided[1]));

        field.forEach(e -> {
            if (e.contains("(")) {
                templateElementFactory.addEntry(readEntry(e));
            } else if (e.equals("required")) {
                templateElementFactory.setRequired();
            } else if (e.equals("constant")) {
                templateElementFactory.setConstant();
            }
        });

        TemplateElement element = templateElementFactory.build();

        if (element == null) {
            System.out.println("模板为空: " + str);
            return null;
        }
        if (element.getName() != null && isBasicPureType(element)) element.setName(null);
        return element;
    }

    private static boolean isSelectTemplate(String fieldStr) {
        String type = readValue(fieldStr, "type");
        if (type == null) return false;
        return fieldStr.contains("selection") && type.equals("Select");
    }

    /**
     * 检查模板元素字符串是否合法,
     * 仅检查完整字符串
     * @param str 原始模板字符串
     * @return
     */
    public static boolean checkValid(String str) {
        // TODO 适应 list map
        // 必须包含类型且有效
        return readValue(str, "type") != null;
    }

    private static final Map<String, DataType> BASIC_PURE_TYPE = new HashMap<>();
    static {
        BASIC_PURE_TYPE.put("int", DataType.Int);
        BASIC_PURE_TYPE.put("char", DataType.Char);
        BASIC_PURE_TYPE.put("short", DataType.Short);
        BASIC_PURE_TYPE.put("long", DataType.Long);
        BASIC_PURE_TYPE.put("String", DataType.String);
        BASIC_PURE_TYPE.put("float", DataType.Float);
        BASIC_PURE_TYPE.put("double", DataType.Double);
        BASIC_PURE_TYPE.put("byte", DataType.Byte);
        BASIC_PURE_TYPE.put("boolean", DataType.Bool);
    }

    /**
     * 纯元素名称只能被设定为 java 保留字段以便确认, 无名模板也会被识别为纯元素
     * 判断是否为纯元素
     * @param element
     * @return
     */
    public static boolean isBasicPureType(TemplateElement element) {
        if (element.getName() == null) return true;
        if (BASIC_PURE_TYPE.containsKey(element.getName())) {
            return BASIC_PURE_TYPE.get(element.getName()) == element.getType();
        }
        return false;
    }

    /**
     * 读取原始字符串的值,
     * 如: type(Int), type -> Int
     * @param str
     * @param key
     * @return null: 不存在 key 或 value 为空
     */
    public static String readValue(String str, String key) {
        if (!str.contains(key)) return null;
        int valueStart = -1;
        int valueEnd = -1;
        int keyPos = 0;
        boolean matched = false;
        // 获取值的开头
        for (int i = 0; i < str.length(); i++) {
            // 当 key 的首字符与 str.charAt(i) 匹配时, keyPos++
            if (str.charAt(i) == key.charAt(keyPos)) {
                matched = true;
                keyPos++;
                // 如果完整匹配
                if (keyPos == key.length()) {
                    // 排除在边界匹配的情况
                    // '('不存在和值不存在
                    if (i + 1 == str.length() || i + 2 == str.length()) throw new IllegalArgumentException("匹配越界");
                    // 匹配完成的下一个字符应当为 '('
                    if (str.charAt(i + 1) != '(') throw new IllegalArgumentException("匹配完成的下一个字符应当为 '('");
                    valueStart = i + 2;
                    break;
                } else {
                    continue;
                }
            }
            // 当 key.charAt(keyPos) 与 str.charAt(i) 不匹配时
            if (str.charAt(i) != key.charAt(keyPos) && matched) {
                matched = false;
                keyPos = 0;
            }
        }
        // 获取值的结尾
        int depth = 1;
        for (int i = valueStart; i < str.length(); i++) {
            if (str.charAt(i) == '(') depth++;
            if (str.charAt(i) == ')') depth--;
            if (depth == 0) {
                valueEnd = i;
                break;
            }
        }
        return removeUselessSpace(str.substring(valueStart, valueEnd));
    }

    public static Map.Entry<String, String> readEntry(String str) {
        int left = findFirstChar(str, '(');
        return new AbstractMap.SimpleEntry<>(str.substring(0, left), str.substring(left + 1, str.length() - 1));
    }

    /**
     * 使用 ';' 分割
     * @param fieldStr
     * @return
     */
    public static List<String> divideFiled(String fieldStr, char split) {
        if (!fieldStr.contains(",")) {
            List<String> res = new ArrayList<>();
            res.add(fieldStr);
            return res;
        }

        int depth = 0;
        int start;
        int end = -1;
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < fieldStr.length(); i++) {
            if (fieldStr.charAt(i) == '(') depth++;
            if (fieldStr.charAt(i) == ')') depth--;
            // 匹配到分隔符
            if (depth == 0 && fieldStr.charAt(i) == split) {
                if (end == -1) {
                    start = 0;
                } else {
                    start = end + 1;
                }
                end = i;
                stringList.add(removeUselessSpace(fieldStr.substring(start, end)));
            }
            if (depth == 0 && i == fieldStr.length() - 1 && end < fieldStr.length()) {
                start = end + 1;
                end = fieldStr.length();
                stringList.add(removeUselessSpace(fieldStr.substring(start, end)));
            }
        }
        return stringList;
    }

    private static String readNameBySplit(String str, int splitIndex) {
        String tempName = str.substring(0, splitIndex);
        tempName = removeUselessSpace(tempName);
        return tempName==null? null : tempName.isEmpty() ? null : tempName;
    }

    /**
     * 使用分隔符 ':' 将字段分割为 String[2], 第一部分为 name, 第二部分为其他,
     * 两部分都可为空
     * @param str
     * @return
     */
    public static String[] divide(String str) {
        if (str == null || str.isEmpty()) return null;
        // 先分解处 name 字段于 res[0], 再将剩余部分放入 res[1]
        String[] resTemp;
        if (str.contains(":")) {
            resTemp = new String[2];
            int splitIndex = findFirstChar(str, ':');
            resTemp[0] = readNameBySplit(str, splitIndex);
            resTemp[1] = str.substring(splitIndex + 1);
        } else {
            return new String[]{null, str};
        }
        resTemp[1] = removeUselessSpace(resTemp[1]);

        return resTemp;
    }

    private static int findFirstChar(String str, char c) {
        if (!str.contains(String.valueOf(c))) return -1;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) return i;
        }
        return -1;
    }

    public static String removeUselessSpace(String str) {
        if (str.isEmpty()) return null;
        if (str.length() == 1 && str.charAt(0) != ' ') {
            return str;
        } else if (str.length() == 1 && str.charAt(0) == ' ') {
            return null;
        }
        if (str.length() == 2) return str.replaceAll(" ", "");
        // 移除开头和结尾的空格
        int start = -1;
        int end = -1;
        for (int i = 0; i < str.length(); i++) {
            // 跳过开头前的空
            if (str.charAt(i) == ' ' && start == -1) {
                continue;
            } else if (str.charAt(i) != ' ' && start == -1) {
                // 如果是开头
                start = i;
                continue;
            }
            // 从起始位置到达空时, 将空位置设置为 end
            if (start != -1 && end == -1 && str.charAt(i) == ' ') {
                end = i;
            }
            // 如果是内容中的空, 取消 end 设置
            if (end != -1 && str.charAt(i) != ' ') {
                end = -1;
            }
            // 如果到结尾都没有空
            if (end == -1 && i == str.length() - 1) {
                end = str.length();
            }
        }
        return str.substring(start, end);
    }

}
