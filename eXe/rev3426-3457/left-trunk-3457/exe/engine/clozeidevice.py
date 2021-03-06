"""
Cloze Idevice. Shows a paragraph where the student must fill in the blanks
"""
import logging
from exe.engine.idevice import Idevice
from exe.engine.path    import Path
from exe.engine.field   import ClozeField, TextAreaField
from exe.engine.persist import Persistable
import Image
log = logging.getLogger(__name__)
class ClozeIdevice(Idevice):
    """
    Holds a paragraph with words missing that the student must fill in
    """
    persistenceVersion = 4
    def __init__(self, parentNode=None):
        """
        Sets up the idevice title and instructions etc
        """
        Idevice.__init__(self, x_(u"Cloze Activity"),
                         x_(u"University of Auckland"), 
                         x_(u"<p>Cloze exercises are texts or "
                             "sentences where students must fill in "
                             "missing words. They are often used for the "
                             "following purposes:</p>"
                             "<ol>"
                             "<li>To check knowledge of core course "
                             "concepts (this could be a pre-check, "
                             "formative exercise, or summative check).</li>"
                             "<li>To check reading comprehension.</li>"
                             "<li>To check vocabulary knowledge.</li>"
                             "<li>To check word formation and/or grammatical "
                             "competence. </li></ol>"),
                         x_(u"<dl>"
                             "  <dt>If your goal is to test understanding "
                             "of core concepts or reading comprehension"
                             "  </dt>"
                             "  <dd>"
                             "    <p>"
                             "  Write a summary of the concept or reading long "
                             " enough to adequately test the target's "
                             "knowledge, but short enough not to "
                             "induce fatigue. Less than one typed page is "
                             "probably adequate, but probably "
                             "considerably less for young students or "
                             "beginners."
                             "    </p>"
                             "    <p>"
                             "Select words in the text that"
                             "are key to understanding the concepts. These"
                             "will probably be verbs, nouns, and key adverbs."
                             "Choose alternatives with one clear answer."
                             "    </p>"
                             "  </dd>"
                             "  <dt>"
                             "If your goal is to test vocabulary knowledge"
                             "  </dt>"
                             "  <dd>"
                             "<p>Write a text using the target vocabulary. This "
                             "text should be coherent and cohesive, and be of "
                             "an appropriate length. Highlight the target "
                             "words in the text. Choose alternatives with one "
                             "clear answer.</p>"
                             "  </dd>"
                             "  <dt>"
                             "If your goal is to test word "
                             "formation/grammar:"
                             "  </dt>"
                             "  <dd>"
                             "  <p>"
                             "Write a text using the "
                             "target forms. This text should be coherent and "
                             "cohesive, and be of an appropriate length. "
                             "Remember that the goal is not vocabulary "
                             "knowledge, so the core meanings of the stem "
                             "words should be well known to the students."
                             "  </p>"
                             "  <p>"
                             "Highlight the target words in the text. Provide "
                             "alternatives with the same word stem, but "
                             "different affixes. It is a good idea to get a "
                             "colleague to test the test/exercise to make "
                             "sure there are no surprises!"
                             "  </p>"
                             "  </dd>"
                             "</dl>"),
                            u"question",
                             parentNode)
        self.instructionsForLearners = TextAreaField(
            x_(u'Instructions'),
            x_(u"""Provide instruction on how the cloze activity should be 
completed. Default text will be entered if there are no changes to this field.
"""),
            x_(u'Read the paragraph below and '
                'fill in the missing words.'))
        self.instructionsForLearners.idevice = self
        self._content = ClozeField(x_(u'Cloze'), 
            x_(u"""<p>Enter the text for the cloze activity in to the cloze field 
by either pasting text from another source or by typing text directly into the 
field.</p><p> To select words to hide, double click on the word to select it and 
click on the Hide/Show Word button below.</p>"""))
        self._content.idevice = self
        self.feedback = TextAreaField(x_(u'Feedback'),
            x_(u'Enter any feedback you wish to provide the learner '
                'with-in the feedback field. This field can be left blank.'))
        self.feedback.idevice = self
        self.emphasis = Idevice.SomeEmphasis
        self.systemResources += ["common.js"]
        self.isCloze = True
    content = property(lambda self: self._content, 
                       doc="Read only, use 'self.content.encodedContent = x' "
                           "instead")
    def getResourcesField(self, this_resource): 
        """ 
        implement the specific resource finding mechanism for this iDevice: 
        """ 
        if hasattr(self, '_content') and hasattr(self._content, 'images'):
            for this_image in self._content.images: 
                if hasattr(this_image, '_imageResource') \
                and this_resource == this_image._imageResource: 
                    return self._content
        if hasattr(self, 'instructionsForLearners')\
        and hasattr(self.instructionsForLearners, 'images'):
            for this_image in self.instructionsForLearners.images: 
                if hasattr(this_image, '_imageResource') \
                and this_resource == this_image._imageResource: 
                    return self.instructionsForLearners
        if hasattr(self, 'feedback') and hasattr(self.feedback, 'images'):
            for this_image in self.feedback.images: 
                if hasattr(this_image, '_imageResource') \
                and this_resource == this_image._imageResource: 
                    return self.feedback
        return None
    def getRichTextFields(self):
        """
        Like getResourcesField(), a general helper to allow nodes to search 
        through all of their fields without having to know the specifics of each
        iDevice type.  
        """
        fields_list = []
        if hasattr(self, '_content'):
            fields_list.append(self._content)
        if hasattr(self, 'instructionsForLearners'):
            fields_list.append(self.instructionsForLearners)
        if hasattr(self, 'feedback'):
            fields_list.append(self.feedback)
        return fields_list
    def upgradeToVersion1(self):
        """
        Upgrades exe to v0.10
        """
        self._upgradeIdeviceToVersion1()
        self.instructionsForLearners = TextAreaField(
            x_(u'Instructions For Learners'),
            x_(u'Put instructions for learners here'),
            x_(u'Read the paragraph below and '
                'fill in the missing words'))
        self.instructionsForLearners.idevice = self
        self.feedback = TextAreaField(x_(u'Feedback'))
        self.feedback.idevice = self
    def upgradeToVersion2(self):
        """
        Upgrades exe to v0.11
        """
        self.content.autoCompletion = True
        self.content.autoCompletionInstruc =  _(u"Allow auto completion when "
                                                u"user filling the gaps.")
    def upgradeToVersion3(self):
        """
        Upgrades to v0.12
        """
        self._upgradeIdeviceToVersion2()
        self.systemResources += ["common.js"]
    def upgradeToVersion4(self):
        """
        Upgrades to v0.20.3
        """
        self.isCloze = True
