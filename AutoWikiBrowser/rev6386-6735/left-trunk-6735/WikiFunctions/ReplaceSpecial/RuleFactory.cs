namespace WikiFunctions.ReplaceSpecial
{
    static class RuleFactory
    {
        public static Rule CreateRule()
        {
            return new Rule();
        }
        public static InTemplateRule CreateInTemplateRule()
        {
            return new InTemplateRule();
        }
        public static TemplateParamRule CreateTemplateParamRule()
        {
            return new TemplateParamRule();
        }
    }
}
