using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using WikiFunctions;
namespace UnitTests
{
    [TestFixture]
    public class SummaryTests : RequiresInitialization
    {
        [Test, Ignore("Known Failing")]
        public void IsCorrect()
        {
            StringBuilder sb = new StringBuilder(300);
            for (int i = 0; i < 256; i++) sb.Append('x');
            Assert.IsFalse(Summary.IsCorrect(sb.ToString()));
            Assert.IsTrue(Summary.IsCorrect(""));
            Assert.IsTrue(Summary.IsCorrect("test"));
            Assert.IsTrue(Summary.IsCorrect("["));
            Assert.IsTrue(Summary.IsCorrect("]"));
            Assert.IsTrue(Summary.IsCorrect("[test]"));
            Assert.IsTrue(Summary.IsCorrect("[test]]"));
            Assert.IsTrue(Summary.IsCorrect("[[]]"));
            Assert.IsTrue(Summary.IsCorrect("[[test]]"));
            Assert.IsTrue(Summary.IsCorrect("[[test]] [[foo]]"));
            Assert.IsTrue(Summary.IsCorrect("[[foo[[]]]"));
            Assert.IsFalse(Summary.IsCorrect("[["));
            Assert.IsFalse(Summary.IsCorrect("[[["));
            Assert.IsFalse(Summary.IsCorrect("[[test]"));
            Assert.IsFalse(Summary.IsCorrect("[[test]] [["));
            Assert.IsFalse(Summary.IsCorrect("[[123456789 123456789 123456789 1[[WP:AWB]]"));
        }
        [Test]
        public void Trim()
        {
            Assert.AreEqual("test", Summary.Trim("test"));
            const string bug1 = @"replaced category 'Actual event ballads' â 'Songs based on actual events' per [[Wikipedia:Categories for discussion/Log/2009 November 6|CfD 2009 Nov 6]]";
            const string waffle = @"some waffle here to make the edit summary too long";
            Assert.AreEqual(bug1, Summary.Trim(bug1));
            Assert.AreEqual(waffle + bug1, Summary.Trim(waffle + bug1));
            Assert.AreEqual(waffle + waffle + @"replaced category 'Actual event ballads' â 'Songs based on actual events' per...", Summary.Trim(waffle + waffle + bug1));
        }
        [Test]
        public void ModifiedSection()
        {
            Assert.AreEqual("", Summary.ModifiedSection("", ""));
            Assert.AreEqual("", Summary.ModifiedSection("foo", "foo"));
            Assert.AreEqual("", Summary.ModifiedSection("foo", "bar"));
            Assert.AreEqual("foo", Summary.ModifiedSection("==foo==\r\n123", "==foo==\r\ntest"));
            Assert.AreEqual("foo", Summary.ModifiedSection("rawr\r\n==foo==\r\n123", "rawr\r\n==foo==\r\ntest"));
            Assert.AreEqual("foo", Summary.ModifiedSection("==foo==\r\n123", "== foo ==\r\ntest"));
            Assert.AreEqual("bar", Summary.ModifiedSection("==foo==\r\n123", "==bar==\r\ntest"));
            Assert.AreEqual("", Summary.ModifiedSection("==foo==\r\nbar", "test\r\n==foo==\r\nbar"));
        }
    }
}
