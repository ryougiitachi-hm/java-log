package per.itachi.java.log.log4j2.common.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(name = "AnonymizedMessagePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"anm", "anonyMsg", "anonymizedMessage"})
public class AnonymizedMessagePatternConverter extends LogEventPatternConverter {

    private static final AnonymizedMessagePatternConverter INSTANCE = new AnonymizedMessagePatternConverter();

    private static final String REGEX_IDCARD_15 = "(?<=[^0-9])[1-9]\\d{5}\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}(?=[^0-9])";

    private static final String REGEX_IDCARD_18 = "(?<=[^0-9])[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx](?=[^0-9])";

    private static final String REGEX_PHONE_NUMBER = "(?<=[^0-9])((\\+86)|(86))?-?1[3-9]\\d{9}(?=[^0-9])";

    private static final String REGEX_FIX_NUMBER = "(?<=[^0-9])0\\d{2,3}-?\\d{7,8}(?=[^0-9])";

    private final List<Pattern> maskList = Arrays
            .asList(Pattern.compile(REGEX_IDCARD_15),
                    Pattern.compile(REGEX_IDCARD_18),
                    Pattern.compile(REGEX_PHONE_NUMBER),
                    Pattern.compile(REGEX_FIX_NUMBER));

    public static AnonymizedMessagePatternConverter newInstance(final String[] options) {
        return INSTANCE;
    }

    /**
     * Constructs an instance of AnonymizedMessagePatternConverter.
     */
    protected AnonymizedMessagePatternConverter() {
        super("AnonymizationMessage", "anonymize");
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        if (maskList == null || maskList.isEmpty()) {
            toAppendTo.append(event.getMessage().getFormattedMessage());
            return;
        }

        String desensitizedMsg = event.getMessage().getFormattedMessage();
        for (Pattern pattern : maskList) {
            desensitizedMsg = desensitize(desensitizedMsg, pattern);
        }
        toAppendTo.append(desensitizedMsg);
    }

    private String desensitize(String originalText, Pattern pattern) {
        Matcher matcher = pattern.matcher(originalText);
        StringBuilder sb = new StringBuilder();

        int curPos = 0;
        while (matcher.find()) {
            sb.append(originalText.subSequence(curPos, matcher.start()));
            sb.append(generateMaskStr(matcher.group().length()));
            curPos = matcher.end();
        }

        if (curPos > 0) {
            sb.append(originalText.substring(curPos));
        }
        return sb.length() == 0 ? originalText : sb.toString();
    }

    /**
     * Currently, using asterisk as mask symbol.
     * */
    private String generateMaskStr(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append("*");
        }
        return builder.toString();
    }
}
