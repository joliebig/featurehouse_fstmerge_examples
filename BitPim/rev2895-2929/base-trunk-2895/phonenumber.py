"""Code for normalising and formatting phone numbers
This doesn't (yet) try to deal with international numbers.
The rule is that if the string contains 10 digits (with an optional
preceding one) then it is reduced to the 10 digits (all non-digit
characters removed, optional leading one removed).
If the string doesn't meet those criteria then it is passed through
as is.
For formatting, 10 digit strings are formatted in standard US
notation.  All others are left as is.
"""
import re
_notdigits=re.compile("[^0-9]*")
_tendigits=re.compile("^[0-9]{10}$")
def normalise(n):
    nums="".join(re.split(_notdigits, n))
    if len(nums)==10:
        return nums
    if len(nums)==11 and nums[0]=="1":
        return nums[1:]
    return n
def format(n):
    if re.match(_tendigits, n) is not None:
        return "(%s) %s-%s" % (n[0:3], n[3:6], n[6:])
    return n
if __name__=='__main__':
    nums=("011441223518046", "+1-123-456-7890", "(123) 456-7890", "0041-2702885504",
          "19175551212", "9175551212", "123 456 7890", "123 456 7890 ext 17")
    for n in nums:
        print "%s\n  norm: %s\n   fmt: %s\n" % (n, normalise(n), format(normalise(n)))
