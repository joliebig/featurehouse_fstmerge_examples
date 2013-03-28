package net.sourceforge.pmd;

import java.util.Map;

import net.sourceforge.pmd.util.CollectionUtil;


public class SourceTypeToRuleLanguageMapper {
    
    private static Map<SourceType, Language> mapSourceTypeOnRuleLanguage = CollectionUtil.mapFrom(
            new SourceType[] { SourceType.JAVA_13, SourceType.JAVA_14,
                    SourceType.JAVA_15, SourceType.JAVA_16, SourceType.JAVA_17, SourceType.JSP, },
            new Language[] { Language.JAVA, Language.JAVA, Language.JAVA,
                    Language.JAVA, Language.JAVA, Language.JSP, });

    private SourceTypeToRuleLanguageMapper() {};
    
    public static Language getMappedLanguage(SourceType sourceType) {
        return mapSourceTypeOnRuleLanguage.get(sourceType);
    }
}
