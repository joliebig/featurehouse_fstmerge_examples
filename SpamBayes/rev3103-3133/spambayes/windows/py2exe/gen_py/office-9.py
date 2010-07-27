"""Microsoft Office 9.0 Object Library"""

makepy_version = '0.4.95'

python_version = 0x20404f0

import win32com.client.CLSIDToClass, pythoncom

import win32com.client.util

from pywintypes import IID

from win32com.client import Dispatch

defaultNamedOptArg=pythoncom.Empty

defaultNamedNotOptArg=pythoncom.Empty

defaultUnnamedArg=pythoncom.Empty

CLSID = IID('{2DF8D04C-5BFA-101B-BDE5-00AA0044DE52}')

MajorVersion = 2

MinorVersion = 1

LibraryFlags = 8

LCID = 0x0

class  constants :
	offPropertyTypeBoolean        =0x2
		offPropertyTypeDate           =0x3
		offPropertyTypeFloat          =0x5
		offPropertyTypeNumber         =0x1
		offPropertyTypeString         =0x4
		msoAlignBottoms               =0x5
		msoAlignCenters               =0x1
		msoAlignLefts                 =0x0
		msoAlignMiddles               =0x4
		msoAlignRights                =0x2
		msoAlignTops                  =0x3
		msoAnimationAppear            =0x20
		msoAnimationBeginSpeaking     =0x4
		msoAnimationCharacterSuccessMajor=0x6
		msoAnimationCheckingSomething =0x67
		msoAnimationDisappear         =0x1f
		msoAnimationEmptyTrash        =0x74
		msoAnimationGestureDown       =0x71
		msoAnimationGestureLeft       =0x72
		msoAnimationGestureRight      =0x13
		msoAnimationGestureUp         =0x73
		msoAnimationGetArtsy          =0x64
		msoAnimationGetAttentionMajor =0xb
		msoAnimationGetAttentionMinor =0xc
		msoAnimationGetTechy          =0x65
		msoAnimationGetWizardy        =0x66
		msoAnimationGoodbye           =0x3
		msoAnimationGreeting          =0x2
		msoAnimationIdle              =0x1
		msoAnimationListensToComputer =0x1a
		msoAnimationLookDown          =0x68
		msoAnimationLookDownLeft      =0x69
		msoAnimationLookDownRight     =0x6a
		msoAnimationLookLeft          =0x6b
		msoAnimationLookRight         =0x6c
		msoAnimationLookUp            =0x6d
		msoAnimationLookUpLeft        =0x6e
		msoAnimationLookUpRight       =0x6f
		msoAnimationPrinting          =0x12
		msoAnimationRestPose          =0x5
		msoAnimationSaving            =0x70
		msoAnimationSearching         =0xd
		msoAnimationSendingMail       =0x19
		msoAnimationThinking          =0x18
		msoAnimationWorkingAtSomething=0x17
		msoAnimationWritingNotingSomething=0x16
		msoLanguageIDExeMode          =0x4
		msoLanguageIDHelp             =0x3
		msoLanguageIDInstall          =0x1
		msoLanguageIDUI               =0x2
		msoLanguageIDUIPrevious       =0x5
		msoArrowheadLengthMedium      =0x2
		msoArrowheadLengthMixed       =-2
		msoArrowheadLong              =0x3
		msoArrowheadShort             =0x1
		msoArrowheadDiamond           =0x5
		msoArrowheadNone              =0x1
		msoArrowheadOpen              =0x3
		msoArrowheadOval              =0x6
		msoArrowheadStealth           =0x4
		msoArrowheadStyleMixed        =-2
		msoArrowheadTriangle          =0x2
		msoArrowheadNarrow            =0x1
		msoArrowheadWide              =0x3
		msoArrowheadWidthMedium       =0x2
		msoArrowheadWidthMixed        =-2
		msoShape16pointStar           =0x5e
		msoShape24pointStar           =0x5f
		msoShape32pointStar           =0x60
		msoShape4pointStar            =0x5b
		msoShape5pointStar            =0x5c
		msoShape8pointStar            =0x5d
		msoShapeActionButtonBackorPrevious=0x81
		msoShapeActionButtonBeginning =0x83
		msoShapeActionButtonCustom    =0x7d
		msoShapeActionButtonDocument  =0x86
		msoShapeActionButtonEnd       =0x84
		msoShapeActionButtonForwardorNext=0x82
		msoShapeActionButtonHelp      =0x7f
		msoShapeActionButtonHome      =0x7e
		msoShapeActionButtonInformation=0x80
		msoShapeActionButtonMovie     =0x88
		msoShapeActionButtonReturn    =0x85
		msoShapeActionButtonSound     =0x87
		msoShapeArc                   =0x19
		msoShapeBalloon               =0x89
		msoShapeBentArrow             =0x29
		msoShapeBentUpArrow           =0x2c
		msoShapeBevel                 =0xf
		msoShapeBlockArc              =0x14
		msoShapeCan                   =0xd
		msoShapeChevron               =0x34
		msoShapeCircularArrow         =0x3c
		msoShapeCloudCallout          =0x6c
		msoShapeCross                 =0xb
		msoShapeCube                  =0xe
		msoShapeCurvedDownArrow       =0x30
		msoShapeCurvedDownRibbon      =0x64
		msoShapeCurvedLeftArrow       =0x2e
		msoShapeCurvedRightArrow      =0x2d
		msoShapeCurvedUpArrow         =0x2f
		msoShapeCurvedUpRibbon        =0x63
		msoShapeDiamond               =0x4
		msoShapeDonut                 =0x12
		msoShapeDoubleBrace           =0x1b
		msoShapeDoubleBracket         =0x1a
		msoShapeDoubleWave            =0x68
		msoShapeDownArrow             =0x24
		msoShapeDownArrowCallout      =0x38
		msoShapeDownRibbon            =0x62
		msoShapeExplosion1            =0x59
		msoShapeExplosion2            =0x5a
		msoShapeFlowchartAlternateProcess=0x3e
		msoShapeFlowchartCard         =0x4b
		msoShapeFlowchartCollate      =0x4f
		msoShapeFlowchartConnector    =0x49
		msoShapeFlowchartData         =0x40
		msoShapeFlowchartDecision     =0x3f
		msoShapeFlowchartDelay        =0x54
		msoShapeFlowchartDirectAccessStorage=0x57
		msoShapeFlowchartDisplay      =0x58
		msoShapeFlowchartDocument     =0x43
		msoShapeFlowchartExtract      =0x51
		msoShapeFlowchartInternalStorage=0x42
		msoShapeFlowchartMagneticDisk =0x56
		msoShapeFlowchartManualInput  =0x47
		msoShapeFlowchartManualOperation=0x48
		msoShapeFlowchartMerge        =0x52
		msoShapeFlowchartMultidocument=0x44
		msoShapeFlowchartOffpageConnector=0x4a
		msoShapeFlowchartOr           =0x4e
		msoShapeFlowchartPredefinedProcess=0x41
		msoShapeFlowchartPreparation  =0x46
		msoShapeFlowchartProcess      =0x3d
		msoShapeFlowchartPunchedTape  =0x4c
		msoShapeFlowchartSequentialAccessStorage=0x55
		msoShapeFlowchartSort         =0x50
		msoShapeFlowchartStoredData   =0x53
		msoShapeFlowchartSummingJunction=0x4d
		msoShapeFlowchartTerminator   =0x45
		msoShapeFoldedCorner          =0x10
		msoShapeHeart                 =0x15
		msoShapeHexagon               =0xa
		msoShapeHorizontalScroll      =0x66
		msoShapeIsoscelesTriangle     =0x7
		msoShapeLeftArrow             =0x22
		msoShapeLeftArrowCallout      =0x36
		msoShapeLeftBrace             =0x1f
		msoShapeLeftBracket           =0x1d
		msoShapeLeftRightArrow        =0x25
		msoShapeLeftRightArrowCallout =0x39
		msoShapeLeftRightUpArrow      =0x28
		msoShapeLeftUpArrow           =0x2b
		msoShapeLightningBolt         =0x16
		msoShapeLineCallout1          =0x6d
		msoShapeLineCallout1AccentBar =0x71
		msoShapeLineCallout1BorderandAccentBar=0x79
		msoShapeLineCallout1NoBorder  =0x75
		msoShapeLineCallout2          =0x6e
		msoShapeLineCallout2AccentBar =0x72
		msoShapeLineCallout2BorderandAccentBar=0x7a
		msoShapeLineCallout2NoBorder  =0x76
		msoShapeLineCallout3          =0x6f
		msoShapeLineCallout3AccentBar =0x73
		msoShapeLineCallout3BorderandAccentBar=0x7b
		msoShapeLineCallout3NoBorder  =0x77
		msoShapeLineCallout4          =0x70
		msoShapeLineCallout4AccentBar =0x74
		msoShapeLineCallout4BorderandAccentBar=0x7c
		msoShapeLineCallout4NoBorder  =0x78
		msoShapeMixed                 =-2
		msoShapeMoon                  =0x18
		msoShapeNoSymbol              =0x13
		msoShapeNotPrimitive          =0x8a
		msoShapeNotchedRightArrow     =0x32
		msoShapeOctagon               =0x6
		msoShapeOval                  =0x9
		msoShapeOvalCallout           =0x6b
		msoShapeParallelogram         =0x2
		msoShapePentagon              =0x33
		msoShapePlaque                =0x1c
		msoShapeQuadArrow             =0x27
		msoShapeQuadArrowCallout      =0x3b
		msoShapeRectangle             =0x1
		msoShapeRectangularCallout    =0x69
		msoShapeRegularPentagon       =0xc
		msoShapeRightArrow            =0x21
		msoShapeRightArrowCallout     =0x35
		msoShapeRightBrace            =0x20
		msoShapeRightBracket          =0x1e
		msoShapeRightTriangle         =0x8
		msoShapeRoundedRectangle      =0x5
		msoShapeRoundedRectangularCallout=0x6a
		msoShapeSmileyFace            =0x11
		msoShapeStripedRightArrow     =0x31
		msoShapeSun                   =0x17
		msoShapeTrapezoid             =0x3
		msoShapeUTurnArrow            =0x2a
		msoShapeUpArrow               =0x23
		msoShapeUpArrowCallout        =0x37
		msoShapeUpDownArrow           =0x26
		msoShapeUpDownArrowCallout    =0x3a
		msoShapeUpRibbon              =0x61
		msoShapeVerticalScroll        =0x65
		msoShapeWave                  =0x67
		msoBalloonButtonAbort         =-8
		msoBalloonButtonBack          =-5
		msoBalloonButtonCancel        =-2
		msoBalloonButtonClose         =-12
		msoBalloonButtonIgnore        =-9
		msoBalloonButtonNext          =-6
		msoBalloonButtonNo            =-4
		msoBalloonButtonNull          =0x0
		msoBalloonButtonOK            =-1
		msoBalloonButtonOptions       =-14
		msoBalloonButtonRetry         =-7
		msoBalloonButtonSearch        =-10
		msoBalloonButtonSnooze        =-11
		msoBalloonButtonTips          =-13
		msoBalloonButtonYes           =-3
		msoBalloonButtonYesToAll      =-15
		msoBalloonErrorBadCharacter   =0x8
		msoBalloonErrorBadPictureRef  =0x4
		msoBalloonErrorBadReference   =0x5
		msoBalloonErrorButtonModeless =0x7
		msoBalloonErrorButtonlessModal=0x6
		msoBalloonErrorCOMFailure     =0x9
		msoBalloonErrorCharNotTopmostForModal=0xa
		msoBalloonErrorNone           =0x0
		msoBalloonErrorOther          =0x1
		msoBalloonErrorOutOfMemory    =0x3
		msoBalloonErrorTooBig         =0x2
		msoBalloonErrorTooManyControls=0xb
		msoBalloonTypeBullets         =0x1
		msoBalloonTypeButtons         =0x0
		msoBalloonTypeNumbers         =0x2
		msoBarBottom                  =0x3
		msoBarFloating                =0x4
		msoBarLeft                    =0x0
		msoBarMenuBar                 =0x6
		msoBarPopup                   =0x5
		msoBarRight                   =0x2
		msoBarTop                     =0x1
		msoBarNoChangeDock            =0x10
		msoBarNoChangeVisible         =0x8
		msoBarNoCustomize             =0x1
		msoBarNoHorizontalDock        =0x40
		msoBarNoMove                  =0x4
		msoBarNoProtection            =0x0
		msoBarNoResize                =0x2
		msoBarNoVerticalDock          =0x20
		msoBarRowFirst                =0x0
		msoBarRowLast                 =-1
		msoBarTypeMenuBar             =0x1
		msoBarTypeNormal              =0x0
		msoBarTypePopup               =0x2
		msoBlackWhiteAutomatic        =0x1
		msoBlackWhiteBlack            =0x8
		msoBlackWhiteBlackTextAndLine =0x6
		msoBlackWhiteDontShow         =0xa
		msoBlackWhiteGrayOutline      =0x5
		msoBlackWhiteGrayScale        =0x2
		msoBlackWhiteHighContrast     =0x7
		msoBlackWhiteInverseGrayScale =0x4
		msoBlackWhiteLightGrayScale   =0x3
		msoBlackWhiteMixed            =-2
		msoBlackWhiteWhite            =0x9
		msoButtonSetAbortRetryIgnore  =0xa
		msoButtonSetBackClose         =0x6
		msoButtonSetBackNextClose     =0x8
		msoButtonSetBackNextSnooze    =0xc
		msoButtonSetCancel            =0x2
		msoButtonSetNextClose         =0x7
		msoButtonSetNone              =0x0
		msoButtonSetOK                =0x1
		msoButtonSetOkCancel          =0x3
		msoButtonSetRetryCancel       =0x9
		msoButtonSetSearchClose       =0xb
		msoButtonSetTipsOptionsClose  =0xd
		msoButtonSetYesAllNoCancel    =0xe
		msoButtonSetYesNo             =0x4
		msoButtonSetYesNoCancel       =0x5
		msoButtonDown                 =-1
		msoButtonMixed                =0x2
		msoButtonUp                   =0x0
		msoButtonAutomatic            =0x0
		msoButtonCaption              =0x2
		msoButtonIcon                 =0x1
		msoButtonIconAndCaption       =0x3
		msoButtonIconAndCaptionBelow  =0xb
		msoButtonIconAndWrapCaption   =0x7
		msoButtonIconAndWrapCaptionBelow=0xf
		msoButtonWrapCaption          =0xe
		msoButtonTextBelow            =0x8
		msoButtonWrapText             =0x4
		msoCalloutAngle30             =0x2
		msoCalloutAngle45             =0x3
		msoCalloutAngle60             =0x4
		msoCalloutAngle90             =0x5
		msoCalloutAngleAutomatic      =0x1
		msoCalloutAngleMixed          =-2
		msoCalloutDropBottom          =0x4
		msoCalloutDropCenter          =0x3
		msoCalloutDropCustom          =0x1
		msoCalloutDropMixed           =-2
		msoCalloutDropTop             =0x2
		msoCalloutFour                =0x4
		msoCalloutMixed               =-2
		msoCalloutOne                 =0x1
		msoCalloutThree               =0x3
		msoCalloutTwo                 =0x2
		msoCharacterSetArabic         =0x1
		msoCharacterSetCyrillic       =0x2
		msoCharacterSetEnglishWesternEuropeanOtherLatinScript=0x3
		msoCharacterSetGreek          =0x4
		msoCharacterSetHebrew         =0x5
		msoCharacterSetJapanese       =0x6
		msoCharacterSetKorean         =0x7
		msoCharacterSetMultilingualUnicode=0x8
		msoCharacterSetSimplifiedChinese=0x9
		msoCharacterSetThai           =0xa
		msoCharacterSetTraditionalChinese=0xb
		msoCharacterSetVietnamese     =0xc
		msoColorTypeMixed             =-2
		msoColorTypeRGB               =0x1
		msoColorTypeScheme            =0x2
		msoComboLabel                 =0x1
		msoComboNormal                =0x0
		msoCommandBarButtonHyperlinkInsertPicture=0x2
		msoCommandBarButtonHyperlinkNone=0x0
		msoCommandBarButtonHyperlinkOpen=0x1
		msoConditionAnyNumberBetween  =0x22
		msoConditionAnytime           =0x19
		msoConditionAnytimeBetween    =0x1a
		msoConditionAtLeast           =0x24
		msoConditionAtMost            =0x23
		msoConditionBeginsWith        =0xb
		msoConditionDoesNotEqual      =0x21
		msoConditionEndsWith          =0xc
		msoConditionEquals            =0x20
		msoConditionFileTypeAllFiles  =0x1
		msoConditionFileTypeBinders   =0x6
		msoConditionFileTypeDatabases =0x7
		msoConditionFileTypeExcelWorkbooks=0x4
		msoConditionFileTypeOfficeFiles=0x2
		msoConditionFileTypePowerPointPresentations=0x5
		msoConditionFileTypeTemplates =0x8
		msoConditionFileTypeWordDocuments=0x3
		msoConditionInTheLast         =0x1f
		msoConditionInTheNext         =0x1e
		msoConditionIncludes          =0x9
		msoConditionIncludesNearEachOther=0xd
		msoConditionIncludesPhrase    =0xa
		msoConditionIsExactly         =0xe
		msoConditionIsNo              =0x28
		msoConditionIsNot             =0xf
		msoConditionIsYes             =0x27
		msoConditionLastMonth         =0x16
		msoConditionLastWeek          =0x13
		msoConditionLessThan          =0x26
		msoConditionMoreThan          =0x25
		msoConditionNextMonth         =0x18
		msoConditionNextWeek          =0x15
		msoConditionOn                =0x1b
		msoConditionOnOrAfter         =0x1c
		msoConditionOnOrBefore        =0x1d
		msoConditionThisMonth         =0x17
		msoConditionThisWeek          =0x14
		msoConditionToday             =0x11
		msoConditionTomorrow          =0x12
		msoConditionYesterday         =0x10
		msoConnectorAnd               =0x1
		msoConnectorOr                =0x2
		msoConnectorCurve             =0x3
		msoConnectorElbow             =0x2
		msoConnectorStraight          =0x1
		msoConnectorTypeMixed         =-2
		msoControlOLEUsageBoth        =0x3
		msoControlOLEUsageClient      =0x2
		msoControlOLEUsageNeither     =0x0
		msoControlOLEUsageServer      =0x1
		msoControlActiveX             =0x16
		msoControlButton              =0x1
		msoControlButtonDropdown      =0x5
		msoControlButtonPopup         =0xc
		msoControlComboBox            =0x4
		msoControlCustom              =0x0
		msoControlDropdown            =0x3
		msoControlEdit                =0x2
		msoControlExpandingGrid       =0x10
		msoControlGauge               =0x13
		msoControlGenericDropdown     =0x8
		msoControlGraphicCombo        =0x14
		msoControlGraphicDropdown     =0x9
		msoControlGraphicPopup        =0xb
		msoControlGrid                =0x12
		msoControlLabel               =0xf
		msoControlOCXDropdown         =0x7
		msoControlPane                =0x15
		msoControlPopup               =0xa
		msoControlSplitButtonMRUPopup =0xe
		msoControlSplitButtonPopup    =0xd
		msoControlSplitDropdown       =0x6
		msoControlSplitExpandingGrid  =0x11
		msoDistributeHorizontally     =0x0
		msoDistributeVertically       =0x1
		msoPropertyTypeBoolean        =0x2
		msoPropertyTypeDate           =0x3
		msoPropertyTypeFloat          =0x5
		msoPropertyTypeNumber         =0x1
		msoPropertyTypeString         =0x4
		msoEditingAuto                =0x0
		msoEditingCorner              =0x1
		msoEditingSmooth              =0x2
		msoEditingSymmetric           =0x3
		msoEncodingArabic             =0x4e8
		msoEncodingArabicASMO         =0x2c4
		msoEncodingArabicAutoDetect   =0xc838
		msoEncodingArabicTransparentASMO=0x2d0
		msoEncodingAutoDetect         =0xc351
		msoEncodingBaltic             =0x4e9
		msoEncodingCentralEuropean    =0x4e2
		msoEncodingCyrillic           =0x4e3
		msoEncodingCyrillicAutoDetect =0xc833
		msoEncodingEBCDICArabic       =0x4fc4
		msoEncodingEBCDICDenmarkNorway=0x4f35
		msoEncodingEBCDICFinlandSweden=0x4f36
		msoEncodingEBCDICFrance       =0x4f49
		msoEncodingEBCDICGermany      =0x4f31
		msoEncodingEBCDICGreek        =0x4fc7
		msoEncodingEBCDICGreekModern  =0x36b
		msoEncodingEBCDICHebrew       =0x4fc8
		msoEncodingEBCDICIcelandic    =0x5187
		msoEncodingEBCDICInternational=0x1f4
		msoEncodingEBCDICItaly        =0x4f38
		msoEncodingEBCDICJapaneseKatakanaExtended=0x4f42
		msoEncodingEBCDICJapaneseKatakanaExtendedAndJapanese=0xc6f2
		msoEncodingEBCDICJapaneseLatinExtendedAndJapanese=0xc6fb
		msoEncodingEBCDICKoreanExtended=0x5161
		msoEncodingEBCDICKoreanExtendedAndKorean=0xc6f5
		msoEncodingEBCDICLatinAmericaSpain=0x4f3c
		msoEncodingEBCDICMultilingualROECELatin2=0x366
		msoEncodingEBCDICRussian      =0x5190
		msoEncodingEBCDICSerbianBulgarian=0x5221
		msoEncodingEBCDICSimplifiedChineseExtendedAndSimplifiedChinese=0xc6f7
		msoEncodingEBCDICThai         =0x5166
		msoEncodingEBCDICTurkish      =0x51a9
		msoEncodingEBCDICTurkishLatin5=0x402
		msoEncodingEBCDICUSCanada     =0x25
		msoEncodingEBCDICUSCanadaAndTraditionalChinese=0xc6f9
		msoEncodingEBCDICUSCanadaandJapanese=0xc6f3
		msoEncodingEBCDICUnitedKingdom=0x4f3d
		msoEncodingEUCChineseSimplifiedChinese=0xcae0
		msoEncodingEUCJapanese        =0xcadc
		msoEncodingEUCKorean          =0xcaed
		msoEncodingEUCTaiwaneseTraditionalChinese=0xcaee
		msoEncodingEuropa3            =0x7149
		msoEncodingExtAlphaLowercase  =0x5223
		msoEncodingGreek              =0x4e5
		msoEncodingGreekAutoDetect    =0xc835
		msoEncodingHZGBSimplifiedChinese=0xcec8
		msoEncodingHebrew             =0x4e7
		msoEncodingIA5German          =0x4e8a
		msoEncodingIA5IRV             =0x4e89
		msoEncodingIA5Norwegian       =0x4e8c
		msoEncodingIA5Swedish         =0x4e8b
		msoEncodingISO2022CNSimplifiedChinese=0xc435
		msoEncodingISO2022CNTraditionalChinese=0xc433
		msoEncodingISO2022JPJISX02011989=0xc42e
		msoEncodingISO2022JPJISX02021984=0xc42d
		msoEncodingISO2022JPNoHalfwidthKatakana=0xc42c
		msoEncodingISO2022KR          =0xc431
		msoEncodingISO6937NonSpacingAccent=0x4f2d
		msoEncodingISO885915Latin9    =0x6fbd
		msoEncodingISO88591Latin1     =0x6faf
		msoEncodingISO88592CentralEurope=0x6fb0
		msoEncodingISO88593Latin3     =0x6fb1
		msoEncodingISO88594Baltic     =0x6fb2
		msoEncodingISO88595Cyrillic   =0x6fb3
		msoEncodingISO88596Arabic     =0x6fb4
		msoEncodingISO88597Greek      =0x6fb5
		msoEncodingISO88598Hebrew     =0x6fb6
		msoEncodingISO88599Turkish    =0x6fb7
		msoEncodingJapaneseAutoDetect =0xc6f4
		msoEncodingJapaneseShiftJIS   =0x3a4
		msoEncodingKOI8R              =0x5182
		msoEncodingKOI8U              =0x556a
		msoEncodingKorean             =0x3b5
		msoEncodingKoreanAutoDetect   =0xc705
		msoEncodingKoreanJohab        =0x551
		msoEncodingMacArabic          =0x2714
		msoEncodingMacCroatia         =0x2762
		msoEncodingMacCyrillic        =0x2717
		msoEncodingMacGreek1          =0x2716
		msoEncodingMacHebrew          =0x2715
		msoEncodingMacIcelandic       =0x275f
		msoEncodingMacJapanese        =0x2711
		msoEncodingMacKorean          =0x2713
		msoEncodingMacLatin2          =0x272d
		msoEncodingMacRoman           =0x2710
		msoEncodingMacRomania         =0x271a
		msoEncodingMacSimplifiedChineseGB2312=0x2718
		msoEncodingMacTraditionalChineseBig5=0x2712
		msoEncodingMacTurkish         =0x2761
		msoEncodingMacUkraine         =0x2721
		msoEncodingOEMArabic          =0x360
		msoEncodingOEMBaltic          =0x307
		msoEncodingOEMCanadianFrench  =0x35f
		msoEncodingOEMCyrillic        =0x357
		msoEncodingOEMCyrillicII      =0x362
		msoEncodingOEMGreek437G       =0x2e1
		msoEncodingOEMHebrew          =0x35e
		msoEncodingOEMIcelandic       =0x35d
		msoEncodingOEMModernGreek     =0x365
		msoEncodingOEMMultilingualLatinI=0x352
		msoEncodingOEMMultilingualLatinII=0x354
		msoEncodingOEMNordic          =0x361
		msoEncodingOEMPortuguese      =0x35c
		msoEncodingOEMTurkish         =0x359
		msoEncodingOEMUnitedStates    =0x1b5
		msoEncodingSimplifiedChineseAutoDetect=0xc6f8
		msoEncodingSimplifiedChineseGBK=0x3a8
		msoEncodingT61                =0x4f25
		msoEncodingTaiwanCNS          =0x4e20
		msoEncodingTaiwanEten         =0x4e22
		msoEncodingTaiwanIBM5550      =0x4e23
		msoEncodingTaiwanTCA          =0x4e21
		msoEncodingTaiwanTeleText     =0x4e24
		msoEncodingTaiwanWang         =0x4e25
		msoEncodingThai               =0x36a
		msoEncodingTraditionalChineseAutoDetect=0xc706
		msoEncodingTraditionalChineseBig5=0x3b6
		msoEncodingTurkish            =0x4e6
		msoEncodingUSASCII            =0x4e9f
		msoEncodingUTF7               =0xfde8
		msoEncodingUTF8               =0xfde9
		msoEncodingUnicodeBigEndian   =0x4b1
		msoEncodingUnicodeLittleEndian=0x4b0
		msoEncodingVietnamese         =0x4ea
		msoEncodingWestern            =0x4e4
		msoMethodGet                  =0x0
		msoMethodPost                 =0x1
		msoExtrusionColorAutomatic    =0x1
		msoExtrusionColorCustom       =0x2
		msoExtrusionColorTypeMixed    =-2
		MsoFarEastLineBreakLanguageJapanese=0x411
		MsoFarEastLineBreakLanguageKorean=0x412
		MsoFarEastLineBreakLanguageSimplifiedChinese=0x804
		MsoFarEastLineBreakLanguageTraditionalChinese=0x404
		msoFeatureInstallNone         =0x0
		msoFeatureInstallOnDemand     =0x1
		msoFeatureInstallOnDemandWithUI=0x2
		msoListbyName                 =0x1
		msoListbyTitle                =0x2
		msoOptionsAdd                 =0x2
		msoOptionsNew                 =0x1
		msoOptionsWithin              =0x3
		msoFileFindSortbyAuthor       =0x1
		msoFileFindSortbyDateCreated  =0x2
		msoFileFindSortbyDateSaved    =0x4
		msoFileFindSortbyFileName     =0x5
		msoFileFindSortbyLastSavedBy  =0x3
		msoFileFindSortbySize         =0x6
		msoFileFindSortbyTitle        =0x7
		msoViewFileInfo               =0x1
		msoViewPreview                =0x2
		msoViewSummaryInfo            =0x3
		msoFileTypeAllFiles           =0x1
		msoFileTypeBinders            =0x6
		msoFileTypeDatabases          =0x7
		msoFileTypeExcelWorkbooks     =0x4
		msoFileTypeOfficeFiles        =0x2
		msoFileTypePowerPointPresentations=0x5
		msoFileTypeTemplates          =0x8
		msoFileTypeWordDocuments      =0x3
		msoFillBackground             =0x5
		msoFillGradient               =0x3
		msoFillMixed                  =-2
		msoFillPatterned              =0x2
		msoFillPicture                =0x6
		msoFillSolid                  =0x1
		msoFillTextured               =0x4
		msoFlipHorizontal             =0x0
		msoFlipVertical               =0x1
		msoGradientColorMixed         =-2
		msoGradientOneColor           =0x1
		msoGradientPresetColors       =0x3
		msoGradientTwoColors          =0x2
		msoGradientDiagonalDown       =0x4
		msoGradientDiagonalUp         =0x3
		msoGradientFromCenter         =0x7
		msoGradientFromCorner         =0x5
		msoGradientFromTitle          =0x6
		msoGradientHorizontal         =0x1
		msoGradientMixed              =-2
		msoGradientVertical           =0x2
		msoHTMLProjectOpenSourceView  =0x1
		msoHTMLProjectOpenTextView    =0x2
		msoHTMLProjectStateDocumentLocked=0x1
		msoHTMLProjectStateDocumentProjectUnlocked=0x3
		msoHTMLProjectStateProjectLocked=0x2
		msoAnchorCenter               =0x2
		msoAnchorNone                 =0x1
		msoHorizontalAnchorMixed      =-2
		msoHyperlinkInlineShape       =0x2
		msoHyperlinkRange             =0x0
		msoHyperlinkShape             =0x1
		msoIconAlert                  =0x2
		msoIconAlertCritical          =0x7
		msoIconAlertInfo              =0x4
		msoIconAlertQuery             =0x6
		msoIconAlertWarning           =0x5
		msoIconNone                   =0x0
		msoIconTip                    =0x3
		msoLanguageIDAfrikaans        =0x436
		msoLanguageIDAlbanian         =0x41c
		msoLanguageIDArabic           =0x401
		msoLanguageIDArabicAlgeria    =0x1401
		msoLanguageIDArabicBahrain    =0x3c01
		msoLanguageIDArabicEgypt      =0xc01
		msoLanguageIDArabicIraq       =0x801
		msoLanguageIDArabicJordan     =0x2c01
		msoLanguageIDArabicKuwait     =0x3401
		msoLanguageIDArabicLebanon    =0x3001
		msoLanguageIDArabicLibya      =0x1001
		msoLanguageIDArabicMorocco    =0x1801
		msoLanguageIDArabicOman       =0x2001
		msoLanguageIDArabicQatar      =0x4001
		msoLanguageIDArabicSyria      =0x2801
		msoLanguageIDArabicTunisia    =0x1c01
		msoLanguageIDArabicUAE        =0x3801
		msoLanguageIDArabicYemen      =0x2401
		msoLanguageIDArmenian         =0x42b
		msoLanguageIDAssamese         =0x44d
		msoLanguageIDAzeriCyrillic    =0x82c
		msoLanguageIDAzeriLatin       =0x42c
		msoLanguageIDBasque           =0x42d
		msoLanguageIDBelgianDutch     =0x813
		msoLanguageIDBelgianFrench    =0x80c
		msoLanguageIDBengali          =0x445
		msoLanguageIDBrazilianPortuguese=0x416
		msoLanguageIDBulgarian        =0x402
		msoLanguageIDBurmese          =0x455
		msoLanguageIDByelorussian     =0x423
		msoLanguageIDCatalan          =0x403
		msoLanguageIDChineseHongKong  =0xc04
		msoLanguageIDChineseMacao     =0x1404
		msoLanguageIDChineseSingapore =0x1004
		msoLanguageIDCroatian         =0x41a
		msoLanguageIDCzech            =0x405
		msoLanguageIDDanish           =0x406
		msoLanguageIDDutch            =0x413
		msoLanguageIDEnglishAUS       =0xc09
		msoLanguageIDEnglishBelize    =0x2809
		msoLanguageIDEnglishCanadian  =0x1009
		msoLanguageIDEnglishCaribbean =0x2409
		msoLanguageIDEnglishIreland   =0x1809
		msoLanguageIDEnglishJamaica   =0x2009
		msoLanguageIDEnglishNewZealand=0x1409
		msoLanguageIDEnglishPhilippines=0x3409
		msoLanguageIDEnglishSouthAfrica=0x1c09
		msoLanguageIDEnglishTrinidad  =0x2c09
		msoLanguageIDEnglishUK        =0x809
		msoLanguageIDEnglishUS        =0x409
		msoLanguageIDEnglishZimbabwe  =0x3009
		msoLanguageIDEstonian         =0x425
		msoLanguageIDFaeroese         =0x438
		msoLanguageIDFarsi            =0x429
		msoLanguageIDFinnish          =0x40b
		msoLanguageIDFrench           =0x40c
		msoLanguageIDFrenchCameroon   =0x2c0c
		msoLanguageIDFrenchCanadian   =0xc0c
		msoLanguageIDFrenchCotedIvoire=0x300c
		msoLanguageIDFrenchLuxembourg =0x140c
		msoLanguageIDFrenchMali       =0x340c
		msoLanguageIDFrenchMonaco     =0x180c
		msoLanguageIDFrenchReunion    =0x200c
		msoLanguageIDFrenchSenegal    =0x280c
		msoLanguageIDFrenchWestIndies =0x1c0c
		msoLanguageIDFrenchZaire      =0x240c
		msoLanguageIDFrisianNetherlands=0x462
		msoLanguageIDGaelicIreland    =0x83c
		msoLanguageIDGaelicScotland   =0x43c
		msoLanguageIDGalician         =0x456
		msoLanguageIDGeorgian         =0x437
		msoLanguageIDGerman           =0x407
		msoLanguageIDGermanAustria    =0xc07
		msoLanguageIDGermanLiechtenstein=0x1407
		msoLanguageIDGermanLuxembourg =0x1007
		msoLanguageIDGreek            =0x408
		msoLanguageIDGujarati         =0x447
		msoLanguageIDHebrew           =0x40d
		msoLanguageIDHindi            =0x439
		msoLanguageIDHungarian        =0x40e
		msoLanguageIDIcelandic        =0x40f
		msoLanguageIDIndonesian       =0x421
		msoLanguageIDItalian          =0x410
		msoLanguageIDJapanese         =0x411
		msoLanguageIDKannada          =0x44b
		msoLanguageIDKashmiri         =0x460
		msoLanguageIDKazakh           =0x43f
		msoLanguageIDKhmer            =0x453
		msoLanguageIDKirghiz          =0x440
		msoLanguageIDKonkani          =0x457
		msoLanguageIDKorean           =0x412
		msoLanguageIDLao              =0x454
		msoLanguageIDLatvian          =0x426
		msoLanguageIDLithuanian       =0x427
		msoLanguageIDMacedonian       =0x42f
		msoLanguageIDMalayBruneiDarussalam=0x83e
		msoLanguageIDMalayalam        =0x44c
		msoLanguageIDMalaysian        =0x43e
		msoLanguageIDMaltese          =0x43a
		msoLanguageIDManipuri         =0x458
		msoLanguageIDMarathi          =0x44e
		msoLanguageIDMexicanSpanish   =0x80a
		msoLanguageIDMixed            =-2
		msoLanguageIDMongolian        =0x450
		msoLanguageIDNepali           =0x461
		msoLanguageIDNoProofing       =0x400
		msoLanguageIDNone             =0x0
		msoLanguageIDNorwegianBokmol  =0x414
		msoLanguageIDNorwegianNynorsk =0x814
		msoLanguageIDOriya            =0x448
		msoLanguageIDPolish           =0x415
		msoLanguageIDPortuguese       =0x816
		msoLanguageIDPunjabi          =0x446
		msoLanguageIDRhaetoRomanic    =0x417
		msoLanguageIDRomanian         =0x418
		msoLanguageIDRomanianMoldova  =0x818
		msoLanguageIDRussian          =0x419
		msoLanguageIDRussianMoldova   =0x819
		msoLanguageIDSamiLappish      =0x43b
		msoLanguageIDSanskrit         =0x44f
		msoLanguageIDSerbianCyrillic  =0xc1a
		msoLanguageIDSerbianLatin     =0x81a
		msoLanguageIDSesotho          =0x430
		msoLanguageIDSimplifiedChinese=0x804
		msoLanguageIDSindhi           =0x459
		msoLanguageIDSlovak           =0x41b
		msoLanguageIDSlovenian        =0x424
		msoLanguageIDSorbian          =0x42e
		msoLanguageIDSpanish          =0x40a
		msoLanguageIDSpanishArgentina =0x2c0a
		msoLanguageIDSpanishBolivia   =0x400a
		msoLanguageIDSpanishChile     =0x340a
		msoLanguageIDSpanishColombia  =0x240a
		msoLanguageIDSpanishCostaRica =0x140a
		msoLanguageIDSpanishDominicanRepublic=0x1c0a
		msoLanguageIDSpanishEcuador   =0x300a
		msoLanguageIDSpanishElSalvador=0x440a
		msoLanguageIDSpanishGuatemala =0x100a
		msoLanguageIDSpanishHonduras  =0x480a
		msoLanguageIDSpanishModernSort=0xc0a
		msoLanguageIDSpanishNicaragua =0x4c0a
		msoLanguageIDSpanishPanama    =0x180a
		msoLanguageIDSpanishParaguay  =0x3c0a
		msoLanguageIDSpanishPeru      =0x280a
		msoLanguageIDSpanishPuertoRico=0x500a
		msoLanguageIDSpanishUruguay   =0x380a
		msoLanguageIDSpanishVenezuela =0x200a
		msoLanguageIDSutu             =0x430
		msoLanguageIDSwahili          =0x441
		msoLanguageIDSwedish          =0x41d
		msoLanguageIDSwedishFinland   =0x81d
		msoLanguageIDSwissFrench      =0x100c
		msoLanguageIDSwissGerman      =0x807
		msoLanguageIDSwissItalian     =0x810
		msoLanguageIDTajik            =0x428
		msoLanguageIDTamil            =0x449
		msoLanguageIDTatar            =0x444
		msoLanguageIDTelugu           =0x44a
		msoLanguageIDThai             =0x41e
		msoLanguageIDTibetan          =0x451
		msoLanguageIDTraditionalChinese=0x404
		msoLanguageIDTsonga           =0x431
		msoLanguageIDTswana           =0x432
		msoLanguageIDTurkish          =0x41f
		msoLanguageIDTurkmen          =0x442
		msoLanguageIDUkrainian        =0x422
		msoLanguageIDUrdu             =0x420
		msoLanguageIDUzbekCyrillic    =0x843
		msoLanguageIDUzbekLatin       =0x443
		msoLanguageIDVenda            =0x433
		msoLanguageIDVietnamese       =0x42a
		msoLanguageIDWelsh            =0x452
		msoLanguageIDXhosa            =0x434
		msoLanguageIDZulu             =0x435
		msoLastModifiedAnyTime        =0x7
		msoLastModifiedLastMonth      =0x5
		msoLastModifiedLastWeek       =0x3
		msoLastModifiedThisMonth      =0x6
		msoLastModifiedThisWeek       =0x4
		msoLastModifiedToday          =0x2
		msoLastModifiedYesterday      =0x1
		msoLineDash                   =0x4
		msoLineDashDot                =0x5
		msoLineDashDotDot             =0x6
		msoLineDashStyleMixed         =-2
		msoLineLongDash               =0x7
		msoLineLongDashDot            =0x8
		msoLineRoundDot               =0x3
		msoLineSolid                  =0x1
		msoLineSquareDot              =0x2
		msoLineSingle                 =0x1
		msoLineStyleMixed             =-2
		msoLineThickBetweenThin       =0x5
		msoLineThickThin              =0x4
		msoLineThinThick              =0x3
		msoLineThinThin               =0x2
		msoMenuAnimationNone          =0x0
		msoMenuAnimationRandom        =0x1
		msoMenuAnimationSlide         =0x3
		msoMenuAnimationUnfold        =0x2
		msoIntegerMixed               =0x8000
		msoSingleMixed                =-2147483648
		msoModeAutoDown               =0x1
		msoModeModal                  =0x0
		msoModeModeless               =0x2
		msoOLEMenuGroupContainer      =0x2
		msoOLEMenuGroupEdit           =0x1
		msoOLEMenuGroupFile           =0x0
		msoOLEMenuGroupHelp           =0x5
		msoOLEMenuGroupNone           =-1
		msoOLEMenuGroupObject         =0x3
		msoOLEMenuGroupWindow         =0x4
		msoOrientationHorizontal      =0x1
		msoOrientationMixed           =-2
		msoOrientationVertical        =0x2
		msoPattern10Percent           =0x2
		msoPattern20Percent           =0x3
		msoPattern25Percent           =0x4
		msoPattern30Percent           =0x5
		msoPattern40Percent           =0x6
		msoPattern50Percent           =0x7
		msoPattern5Percent            =0x1
		msoPattern60Percent           =0x8
		msoPattern70Percent           =0x9
		msoPattern75Percent           =0xa
		msoPattern80Percent           =0xb
		msoPattern90Percent           =0xc
		msoPatternDarkDownwardDiagonal=0xf
		msoPatternDarkHorizontal      =0xd
		msoPatternDarkUpwardDiagonal  =0x10
		msoPatternDarkVertical        =0xe
		msoPatternDashedDownwardDiagonal=0x1c
		msoPatternDashedHorizontal    =0x20
		msoPatternDashedUpwardDiagonal=0x1b
		msoPatternDashedVertical      =0x1f
		msoPatternDiagonalBrick       =0x28
		msoPatternDivot               =0x2e
		msoPatternDottedDiamond       =0x18
		msoPatternDottedGrid          =0x2d
		msoPatternHorizontalBrick     =0x23
		msoPatternLargeCheckerBoard   =0x24
		msoPatternLargeConfetti       =0x21
		msoPatternLargeGrid           =0x22
		msoPatternLightDownwardDiagonal=0x15
		msoPatternLightHorizontal     =0x13
		msoPatternLightUpwardDiagonal =0x16
		msoPatternLightVertical       =0x14
		msoPatternMixed               =-2
		msoPatternNarrowHorizontal    =0x1e
		msoPatternNarrowVertical      =0x1d
		msoPatternOutlinedDiamond     =0x29
		msoPatternPlaid               =0x2a
		msoPatternShingle             =0x2f
		msoPatternSmallCheckerBoard   =0x11
		msoPatternSmallConfetti       =0x25
		msoPatternSmallGrid           =0x17
		msoPatternSolidDiamond        =0x27
		msoPatternSphere              =0x2b
		msoPatternTrellis             =0x12
		msoPatternWave                =0x30
		msoPatternWeave               =0x2c
		msoPatternWideDownwardDiagonal=0x19
		msoPatternWideUpwardDiagonal  =0x1a
		msoPatternZigZag              =0x26
		msoPictureAutomatic           =0x1
		msoPictureBlackAndWhite       =0x3
		msoPictureGrayscale           =0x2
		msoPictureMixed               =-2
		msoPictureWatermark           =0x4
		msoExtrusionBottom            =0x2
		msoExtrusionBottomLeft        =0x3
		msoExtrusionBottomRight       =0x1
		msoExtrusionLeft              =0x6
		msoExtrusionNone              =0x5
		msoExtrusionRight             =0x4
		msoExtrusionTop               =0x8
		msoExtrusionTopLeft           =0x9
		msoExtrusionTopRight          =0x7
		msoPresetExtrusionDirectionMixed=-2
		msoGradientBrass              =0x14
		msoGradientCalmWater          =0x8
		msoGradientChrome             =0x15
		msoGradientChromeII           =0x16
		msoGradientDaybreak           =0x4
		msoGradientDesert             =0x6
		msoGradientEarlySunset        =0x1
		msoGradientFire               =0x9
		msoGradientFog                =0xa
		msoGradientGold               =0x12
		msoGradientGoldII             =0x13
		msoGradientHorizon            =0x5
		msoGradientLateSunset         =0x2
		msoGradientMahogany           =0xf
		msoGradientMoss               =0xb
		msoGradientNightfall          =0x3
		msoGradientOcean              =0x7
		msoGradientParchment          =0xe
		msoGradientPeacock            =0xc
		msoGradientRainbow            =0x10
		msoGradientRainbowII          =0x11
		msoGradientSapphire           =0x18
		msoGradientSilver             =0x17
		msoGradientWheat              =0xd
		msoPresetGradientMixed        =-2
		msoLightingBottom             =0x8
		msoLightingBottomLeft         =0x7
		msoLightingBottomRight        =0x9
		msoLightingLeft               =0x4
		msoLightingNone               =0x5
		msoLightingRight              =0x6
		msoLightingTop                =0x2
		msoLightingTopLeft            =0x1
		msoLightingTopRight           =0x3
		msoPresetLightingDirectionMixed=-2
		msoLightingBright             =0x3
		msoLightingDim                =0x1
		msoLightingNormal             =0x2
		msoPresetLightingSoftnessMixed=-2
		msoMaterialMatte              =0x1
		msoMaterialMetal              =0x3
		msoMaterialPlastic            =0x2
		msoMaterialWireFrame          =0x4
		msoPresetMaterialMixed        =-2
		msoTextEffect1                =0x0
		msoTextEffect10               =0x9
		msoTextEffect11               =0xa
		msoTextEffect12               =0xb
		msoTextEffect13               =0xc
		msoTextEffect14               =0xd
		msoTextEffect15               =0xe
		msoTextEffect16               =0xf
		msoTextEffect17               =0x10
		msoTextEffect18               =0x11
		msoTextEffect19               =0x12
		msoTextEffect2                =0x1
		msoTextEffect20               =0x13
		msoTextEffect21               =0x14
		msoTextEffect22               =0x15
		msoTextEffect23               =0x16
		msoTextEffect24               =0x17
		msoTextEffect25               =0x18
		msoTextEffect26               =0x19
		msoTextEffect27               =0x1a
		msoTextEffect28               =0x1b
		msoTextEffect29               =0x1c
		msoTextEffect3                =0x2
		msoTextEffect30               =0x1d
		msoTextEffect4                =0x3
		msoTextEffect5                =0x4
		msoTextEffect6                =0x5
		msoTextEffect7                =0x6
		msoTextEffect8                =0x7
		msoTextEffect9                =0x8
		msoTextEffectMixed            =-2
		msoTextEffectShapeArchDownCurve=0xa
		msoTextEffectShapeArchDownPour=0xe
		msoTextEffectShapeArchUpCurve =0x9
		msoTextEffectShapeArchUpPour  =0xd
		msoTextEffectShapeButtonCurve =0xc
		msoTextEffectShapeButtonPour  =0x10
		msoTextEffectShapeCanDown     =0x14
		msoTextEffectShapeCanUp       =0x13
		msoTextEffectShapeCascadeDown =0x28
		msoTextEffectShapeCascadeUp   =0x27
		msoTextEffectShapeChevronDown =0x6
		msoTextEffectShapeChevronUp   =0x5
		msoTextEffectShapeCircleCurve =0xb
		msoTextEffectShapeCirclePour  =0xf
		msoTextEffectShapeCurveDown   =0x12
		msoTextEffectShapeCurveUp     =0x11
		msoTextEffectShapeDeflate     =0x1a
		msoTextEffectShapeDeflateBottom=0x1c
		msoTextEffectShapeDeflateInflate=0x1f
		msoTextEffectShapeDeflateInflateDeflate=0x20
		msoTextEffectShapeDeflateTop  =0x1e
		msoTextEffectShapeDoubleWave1 =0x17
		msoTextEffectShapeDoubleWave2 =0x18
		msoTextEffectShapeFadeDown    =0x24
		msoTextEffectShapeFadeLeft    =0x22
		msoTextEffectShapeFadeRight   =0x21
		msoTextEffectShapeFadeUp      =0x23
		msoTextEffectShapeInflate     =0x19
		msoTextEffectShapeInflateBottom=0x1b
		msoTextEffectShapeInflateTop  =0x1d
		msoTextEffectShapeMixed       =-2
		msoTextEffectShapePlainText   =0x1
		msoTextEffectShapeRingInside  =0x7
		msoTextEffectShapeRingOutside =0x8
		msoTextEffectShapeSlantDown   =0x26
		msoTextEffectShapeSlantUp     =0x25
		msoTextEffectShapeStop        =0x2
		msoTextEffectShapeTriangleDown=0x4
		msoTextEffectShapeTriangleUp  =0x3
		msoTextEffectShapeWave1       =0x15
		msoTextEffectShapeWave2       =0x16
		msoPresetTextureMixed         =-2
		msoTextureBlueTissuePaper     =0x11
		msoTextureBouquet             =0x14
		msoTextureBrownMarble         =0xb
		msoTextureCanvas              =0x2
		msoTextureCork                =0x15
		msoTextureDenim               =0x3
		msoTextureFishFossil          =0x7
		msoTextureGranite             =0xc
		msoTextureGreenMarble         =0x9
		msoTextureMediumWood          =0x18
		msoTextureNewsprint           =0xd
		msoTextureOak                 =0x17
		msoTexturePaperBag            =0x6
		msoTexturePapyrus             =0x1
		msoTextureParchment           =0xf
		msoTexturePinkTissuePaper     =0x12
		msoTexturePurpleMesh          =0x13
		msoTextureRecycledPaper       =0xe
		msoTextureSand                =0x8
		msoTextureStationery          =0x10
		msoTextureWalnut              =0x16
		msoTextureWaterDroplets       =0x5
		msoTextureWhiteMarble         =0xa
		msoTextureWovenMat            =0x4
		msoPresetThreeDFormatMixed    =-2
		msoThreeD1                    =0x1
		msoThreeD10                   =0xa
		msoThreeD11                   =0xb
		msoThreeD12                   =0xc
		msoThreeD13                   =0xd
		msoThreeD14                   =0xe
		msoThreeD15                   =0xf
		msoThreeD16                   =0x10
		msoThreeD17                   =0x11
		msoThreeD18                   =0x12
		msoThreeD19                   =0x13
		msoThreeD2                    =0x2
		msoThreeD20                   =0x14
		msoThreeD3                    =0x3
		msoThreeD4                    =0x4
		msoThreeD5                    =0x5
		msoThreeD6                    =0x6
		msoThreeD7                    =0x7
		msoThreeD8                    =0x8
		msoThreeD9                    =0x9
		msoScaleFromBottomRight       =0x2
		msoScaleFromMiddle            =0x1
		msoScaleFromTopLeft           =0x0
		msoScreenSize1024x768         =0x4
		msoScreenSize1152x882         =0x5
		msoScreenSize1152x900         =0x6
		msoScreenSize1280x1024        =0x7
		msoScreenSize1600x1200        =0x8
		msoScreenSize1800x1440        =0x9
		msoScreenSize1920x1200        =0xa
		msoScreenSize544x376          =0x0
		msoScreenSize640x480          =0x1
		msoScreenSize720x512          =0x2
		msoScreenSize800x600          =0x3
		msoScriptLanguageASP          =0x3
		msoScriptLanguageJava         =0x1
		msoScriptLanguageOther        =0x4
		msoScriptLanguageVisualBasic  =0x2
		msoScriptLocationInBody       =0x2
		msoScriptLocationInHead       =0x1
		msoSegmentCurve               =0x1
		msoSegmentLine                =0x0
		msoShadow1                    =0x1
		msoShadow10                   =0xa
		msoShadow11                   =0xb
		msoShadow12                   =0xc
		msoShadow13                   =0xd
		msoShadow14                   =0xe
		msoShadow15                   =0xf
		msoShadow16                   =0x10
		msoShadow17                   =0x11
		msoShadow18                   =0x12
		msoShadow19                   =0x13
		msoShadow2                    =0x2
		msoShadow20                   =0x14
		msoShadow3                    =0x3
		msoShadow4                    =0x4
		msoShadow5                    =0x5
		msoShadow6                    =0x6
		msoShadow7                    =0x7
		msoShadow8                    =0x8
		msoShadow9                    =0x9
		msoShadowMixed                =-2
		msoAutoShape                  =0x1
		msoCallout                    =0x2
		msoChart                      =0x3
		msoComment                    =0x4
		msoEmbeddedOLEObject          =0x7
		msoFormControl                =0x8
		msoFreeform                   =0x5
		msoGroup                      =0x6
		msoLine                       =0x9
		msoLinkedOLEObject            =0xa
		msoLinkedPicture              =0xb
		msoMedia                      =0x10
		msoOLEControlObject           =0xc
		msoPicture                    =0xd
		msoPlaceholder                =0xe
		msoScriptAnchor               =0x12
		msoShapeTypeMixed             =-2
		msoTable                      =0x13
		msoTextBox                    =0x11
		msoTextEffect                 =0xf
		msoSortByFileName             =0x1
		msoSortByFileType             =0x3
		msoSortByLastModified         =0x4
		msoSortBySize                 =0x2
		msoSortOrderAscending         =0x1
		msoSortOrderDescending        =0x2
		msoTextEffectAlignmentCentered=0x2
		msoTextEffectAlignmentLeft    =0x1
		msoTextEffectAlignmentLetterJustify=0x4
		msoTextEffectAlignmentMixed   =-2
		msoTextEffectAlignmentRight   =0x3
		msoTextEffectAlignmentStretchJustify=0x6
		msoTextEffectAlignmentWordJustify=0x5
		msoTextOrientationDownward    =0x3
		msoTextOrientationHorizontal  =0x1
		msoTextOrientationHorizontalRotatedFarEast=0x6
		msoTextOrientationMixed       =-2
		msoTextOrientationUpward      =0x2
		msoTextOrientationVertical    =0x5
		msoTextOrientationVerticalFarEast=0x4
		msoTexturePreset              =0x1
		msoTextureTypeMixed           =-2
		msoTextureUserDefined         =0x2
		msoCTrue                      =0x1
		msoFalse                      =0x0
		msoTriStateMixed              =-2
		msoTriStateToggle             =-3
		msoTrue                       =-1
		msoAnchorBottom               =0x4
		msoAnchorBottomBaseLine       =0x5
		msoAnchorMiddle               =0x3
		msoAnchorTop                  =0x1
		msoAnchorTopBaseline          =0x2
		msoVerticalAnchorMixed        =-2
		msoWizardActActive            =0x1
		msoWizardActInactive          =0x0
		msoWizardActResume            =0x3
		msoWizardActSuspend           =0x2
		msoWizardMsgLocalStateOff     =0x2
		msoWizardMsgLocalStateOn      =0x1
		msoWizardMsgResuming          =0x5
		msoWizardMsgShowHelp          =0x3
		msoWizardMsgSuspending        =0x4
		msoBringForward               =0x2
		msoBringInFrontOfText         =0x4
		msoBringToFront               =0x0
		msoSendBackward               =0x3
		msoSendBehindText             =0x5
		msoSendToBack                 =0x1
from win32com.client import DispatchBaseClass class  Adjustments (DispatchBaseClass) :
	CLSID = IID('{000C0310-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Item(self, Index=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(0, LCID, 2, (4, 0), ((3, 1),),Index
			)
 def SetItem(self, Index=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(0, LCID, 4, (24, 0), ((3, 1), (4, 1)),Index
			, arg1)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (2, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(0, LCID, 2, (4, 0), ((3, 1),),Index
			)
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __len__(self):

		return self._ApplyTypes_(*(2, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  AnswerWizard (DispatchBaseClass) :
	CLSID = IID('{000C0360-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def ClearFileList(self):

		return self._oleobj_.InvokeTypes(1610809346, LCID, 1, (24, 0), (),)
 def ResetFileList(self):

		return self._oleobj_.InvokeTypes(1610809347, LCID, 1, (24, 0), (),)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		
		"Files": (1610809345, 2, (9, 0), (), "Files", '{000C0361-0000-0000-C000-000000000046}'),
		"Parent": (1610809344, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
class  AnswerWizardFiles (DispatchBaseClass) :
	CLSID = IID('{000C0361-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Add(self, FileName=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1610809347, LCID, 1, (24, 0), ((8, 1),),FileName
			)
 def Delete(self, FileName=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1610809348, LCID, 1, (24, 0), ((8, 1),),FileName
			)
 def Item(self, Index=defaultNamedNotOptArg):

		
		return self._oleobj_.InvokeTypes(0, LCID, 2, (8, 0), ((3, 1),),Index
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (1610809346, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Parent": (1610809344, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		
		return self._oleobj_.InvokeTypes(0, LCID, 2, (8, 0), ((3, 1),),Index
			)
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __len__(self):

		return self._ApplyTypes_(*(1610809346, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  Assistant (DispatchBaseClass) :
	CLSID = IID('{000C0322-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def ActivateWizard(self, WizardID=defaultNamedNotOptArg, act=defaultNamedNotOptArg, Animation=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610809353, LCID, 1, (24, 0), ((3, 1), (3, 1), (12, 17)),WizardID
			, act, Animation)
 def EndWizard(self, WizardID=defaultNamedNotOptArg, varfSuccess=defaultNamedNotOptArg, Animation=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610809352, LCID, 1, (24, 0), ((3, 1), (11, 1), (12, 17)),WizardID
			, varfSuccess, Animation)
 def Help(self):

		return self._oleobj_.InvokeTypes(1610809350, LCID, 1, (24, 0), (),)
 def Move(self, xLeft=defaultNamedNotOptArg, yTop=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1610809345, LCID, 1, (24, 0), ((3, 1), (3, 1)),xLeft
			, yTop)
 def ResetTips(self):

		return self._oleobj_.InvokeTypes(1610809354, LCID, 1, (24, 0), (),)
 def StartWizard(self, On=defaultNamedNotOptArg, Callback=defaultNamedNotOptArg, PrivateX=defaultNamedNotOptArg, Animation=defaultNamedOptArg
			, CustomTeaser=defaultNamedOptArg, Top=defaultNamedOptArg, Left=defaultNamedOptArg, Bottom=defaultNamedOptArg, Right=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610809351, LCID, 1, (3, 0), ((11, 1), (8, 1), (3, 1), (12, 17), (12, 17), (12, 17), (12, 17), (12, 17), (12, 17)),On
			, Callback, PrivateX, Animation, CustomTeaser, Top
			, Left, Bottom, Right)

	_prop_map_get_ = {
		"Animation": (1610809359, 2, (3, 0), (), "Animation", None),
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"AssistWithAlerts": (1610809367, 2, (11, 0), (), "AssistWithAlerts", None),
		"AssistWithHelp": (1610809363, 2, (11, 0), (), "AssistWithHelp", None),
		"AssistWithWizards": (1610809365, 2, (11, 0), (), "AssistWithWizards", None),
		"BalloonError": (1610809356, 2, (3, 0), (), "BalloonError", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"FeatureTips": (1610809373, 2, (11, 0), (), "FeatureTips", None),
		"FileName": (1610809388, 2, (8, 0), (), "FileName", None),
		"GuessHelp": (1610809383, 2, (11, 0), (), "GuessHelp", None),
		"HighPriorityTips": (1610809379, 2, (11, 0), (), "HighPriorityTips", None),
		"Item": (0, 2, (8, 0), (), "Item", None),
		"KeyboardShortcutTips": (1610809377, 2, (11, 0), (), "KeyboardShortcutTips", None),
		"Left": (1610809348, 2, (3, 0), (), "Left", None),
		"MouseTips": (1610809375, 2, (11, 0), (), "MouseTips", None),
		"MoveWhenInTheWay": (1610809369, 2, (11, 0), (), "MoveWhenInTheWay", None),
		"Name": (1610809390, 2, (8, 0), (), "Name", None),
		
		"NewBalloon": (1610809355, 2, (9, 0), (), "NewBalloon", '{000C0324-0000-0000-C000-000000000046}'),
		"On": (1610809391, 2, (11, 0), (), "On", None),
		"Parent": (1610809344, 2, (9, 0), (), "Parent", None),
		"Reduced": (1610809361, 2, (11, 0), (), "Reduced", None),
		"SearchWhenProgramming": (1610809385, 2, (11, 0), (), "SearchWhenProgramming", None),
		"Sounds": (1610809371, 2, (11, 0), (), "Sounds", None),
		"TipOfDay": (1610809381, 2, (11, 0), (), "TipOfDay", None),
		"Top": (1610809346, 2, (3, 0), (), "Top", None),
		"Visible": (1610809357, 2, (11, 0), (), "Visible", None),
	}
		_prop_map_put_ = {
		"Animation": ((1610809359, LCID, 4, 0),()),
		"AssistWithAlerts": ((1610809367, LCID, 4, 0),()),
		"AssistWithHelp": ((1610809363, LCID, 4, 0),()),
		"AssistWithWizards": ((1610809365, LCID, 4, 0),()),
		"FeatureTips": ((1610809373, LCID, 4, 0),()),
		"FileName": ((1610809388, LCID, 4, 0),()),
		"GuessHelp": ((1610809383, LCID, 4, 0),()),
		"HighPriorityTips": ((1610809379, LCID, 4, 0),()),
		"KeyboardShortcutTips": ((1610809377, LCID, 4, 0),()),
		"Left": ((1610809348, LCID, 4, 0),()),
		"MouseTips": ((1610809375, LCID, 4, 0),()),
		"MoveWhenInTheWay": ((1610809369, LCID, 4, 0),()),
		"On": ((1610809391, LCID, 4, 0),()),
		"Reduced": ((1610809361, LCID, 4, 0),()),
		"SearchWhenProgramming": ((1610809385, LCID, 4, 0),()),
		"Sounds": ((1610809371, LCID, 4, 0),()),
		"TipOfDay": ((1610809381, LCID, 4, 0),()),
		"Top": ((1610809346, LCID, 4, 0),()),
		"Visible": ((1610809357, LCID, 4, 0),()),
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (8, 0), (), "Item", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  Balloon (DispatchBaseClass) :
	CLSID = IID('{000C0324-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Close(self):

		return self._oleobj_.InvokeTypes(1610809368, LCID, 1, (24, 0), (),)
 def SetAvoidRectangle(self, Left=defaultNamedNotOptArg, Top=defaultNamedNotOptArg, Right=defaultNamedNotOptArg, Bottom=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1610809365, LCID, 1, (24, 0), ((3, 1), (3, 1), (3, 1), (3, 1)),Left
			, Top, Right, Bottom)
 def Show(self):

		return self._oleobj_.InvokeTypes(1610809367, LCID, 1, (3, 0), (),)

	_prop_map_get_ = {
		"Animation": (1610809357, 2, (3, 0), (), "Animation", None),
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"BalloonType": (1610809347, 2, (3, 0), (), "BalloonType", None),
		"Button": (1610809359, 2, (3, 0), (), "Button", None),
		"Callback": (1610809361, 2, (8, 0), (), "Callback", None),
		"Checkboxes": (1610809345, 2, (9, 0), (), "Checkboxes", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Heading": (1610809351, 2, (8, 0), (), "Heading", None),
		"Icon": (1610809349, 2, (3, 0), (), "Icon", None),
		"Labels": (1610809346, 2, (9, 0), (), "Labels", None),
		"Mode": (1610809355, 2, (3, 0), (), "Mode", None),
		"Name": (1610809366, 2, (8, 0), (), "Name", None),
		"Parent": (1610809344, 2, (9, 0), (), "Parent", None),
		"Private": (1610809363, 2, (3, 0), (), "Private", None),
		"Text": (1610809353, 2, (8, 0), (), "Text", None),
	}
		_prop_map_put_ = {
		"Animation": ((1610809357, LCID, 4, 0),()),
		"BalloonType": ((1610809347, LCID, 4, 0),()),
		"Button": ((1610809359, LCID, 4, 0),()),
		"Callback": ((1610809361, LCID, 4, 0),()),
		"Heading": ((1610809351, LCID, 4, 0),()),
		"Icon": ((1610809349, LCID, 4, 0),()),
		"Mode": ((1610809355, LCID, 4, 0),()),
		"Private": ((1610809363, LCID, 4, 0),()),
		"Text": ((1610809353, LCID, 4, 0),()),
	}
class  BalloonCheckbox (DispatchBaseClass) :
	CLSID = IID('{000C0328-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Checked": (1610809347, 2, (11, 0), (), "Checked", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Item": (0, 2, (8, 0), (), "Item", None),
		"Name": (1610809345, 2, (8, 0), (), "Name", None),
		"Parent": (1610809346, 2, (9, 0), (), "Parent", None),
		"Text": (1610809349, 2, (8, 0), (), "Text", None),
	}
		_prop_map_put_ = {
		"Checked": ((1610809347, LCID, 4, 0),()),
		"Text": ((1610809349, LCID, 4, 0),()),
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (8, 0), (), "Item", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  BalloonCheckboxes (DispatchBaseClass) :
	CLSID = IID('{000C0326-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((3, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', None, UnicodeToString=0)

		return ret

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (1610809347, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Name": (1610809344, 2, (8, 0), (), "Name", None),
		"Parent": (1610809345, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
		"Count": ((1610809347, LCID, 4, 0),()),
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((3, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', None, UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),None)
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(1610809347, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  BalloonLabel (DispatchBaseClass) :
	CLSID = IID('{000C0330-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Item": (0, 2, (8, 0), (), "Item", None),
		"Name": (1610809345, 2, (8, 0), (), "Name", None),
		"Parent": (1610809346, 2, (9, 0), (), "Parent", None),
		"Text": (1610809347, 2, (8, 0), (), "Text", None),
	}
		_prop_map_put_ = {
		"Text": ((1610809347, LCID, 4, 0),()),
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (8, 0), (), "Item", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  BalloonLabels (DispatchBaseClass) :
	CLSID = IID('{000C032E-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((3, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', None, UnicodeToString=0)

		return ret

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (1610809347, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Name": (1610809344, 2, (8, 0), (), "Name", None),
		"Parent": (1610809345, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
		"Count": ((1610809347, LCID, 4, 0),()),
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((3, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', None, UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),None)
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(1610809347, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  COMAddIn (DispatchBaseClass) :
	CLSID = IID('{000C033A-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Connect": (6, 2, (11, 0), (), "Connect", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Description": (0, 2, (8, 0), (), "Description", None),
		"Guid": (4, 2, (8, 0), (), "Guid", None),
		"Object": (7, 2, (9, 0), (), "Object", None),
		"Parent": (8, 2, (9, 0), (), "Parent", None),
		"ProgId": (3, 2, (8, 0), (), "ProgId", None),
	}
		_prop_map_put_ = {
		"Connect": ((6, LCID, 4, 0),()),
		"Description": ((0, LCID, 4, 0),()),
		"Object": ((7, LCID, 4, 0),()),
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (8, 0), (), "Description", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  COMAddIns (DispatchBaseClass) :
	CLSID = IID('{000C0339-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((16396, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C033A-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def SetAppModal(self, varfModal=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(4, LCID, 1, (24, 0), ((11, 1),),varfModal
			)
 def Update(self):

		return self._oleobj_.InvokeTypes(2, LCID, 1, (24, 0), (),)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (1, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Parent": (3, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((16396, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C033A-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C033A-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(1, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  CalloutFormat (DispatchBaseClass) :
	CLSID = IID('{000C0311-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def AutomaticLength(self):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), (),)
 def CustomDrop(self, Drop=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(11, LCID, 1, (24, 0), ((4, 1),),Drop
			)
 def CustomLength(self, Length=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(12, LCID, 1, (24, 0), ((4, 1),),Length
			)
 def PresetDrop(self, DropType=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(13, LCID, 1, (24, 0), ((3, 1),),DropType
			)

	_prop_map_get_ = {
		"Accent": (100, 2, (3, 0), (), "Accent", None),
		"Angle": (101, 2, (3, 0), (), "Angle", None),
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"AutoAttach": (102, 2, (3, 0), (), "AutoAttach", None),
		"AutoLength": (103, 2, (3, 0), (), "AutoLength", None),
		"Border": (104, 2, (3, 0), (), "Border", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Drop": (105, 2, (4, 0), (), "Drop", None),
		"DropType": (106, 2, (3, 0), (), "DropType", None),
		"Gap": (107, 2, (4, 0), (), "Gap", None),
		"Length": (108, 2, (4, 0), (), "Length", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"Type": (109, 2, (3, 0), (), "Type", None),
	}
		_prop_map_put_ = {
		"Accent": ((100, LCID, 4, 0),()),
		"Angle": ((101, LCID, 4, 0),()),
		"AutoAttach": ((102, LCID, 4, 0),()),
		"Border": ((104, LCID, 4, 0),()),
		"Gap": ((107, LCID, 4, 0),()),
		"Type": ((109, LCID, 4, 0),()),
	}
class  ColorFormat (DispatchBaseClass) :
	CLSID = IID('{000C0312-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"RGB": (0, 2, (3, 0), (), "RGB", None),
		"SchemeColor": (100, 2, (3, 0), (), "SchemeColor", None),
		"Type": (101, 2, (3, 0), (), "Type", None),
	}
		_prop_map_put_ = {
		"RGB": ((0, LCID, 4, 0),()),
		"SchemeColor": ((100, LCID, 4, 0),()),
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (3, 0), (), "RGB", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  CommandBar (DispatchBaseClass) :
	CLSID = IID('{000C0304-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Delete(self):

		return self._oleobj_.InvokeTypes(1610874884, LCID, 1, (24, 0), (),)
 def FindControl(self, Type=defaultNamedOptArg, Id=defaultNamedOptArg, Tag=defaultNamedOptArg, Visible=defaultNamedOptArg
			, Recursive=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874887, LCID, 1, (9, 0), ((12, 17), (12, 17), (12, 17), (12, 17), (12, 17)),Type
			, Id, Tag, Visible, Recursive)

		if ret is not None:

			ret = Dispatch(ret, 'FindControl', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def GetaccDefaultAction(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5013, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccDescription(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5005, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelp(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5008, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelpTopic(self, pszHelpFile=pythoncom.Missing, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5009, 2, (3, 0), ((16392, 2), (12, 17)), 'GetaccHelpTopic', None,pszHelpFile
			, varChild)
 def GetaccKeyboardShortcut(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5010, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccName(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5003, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccRole(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5006, 2, (12, 0), ((12, 17),), 'GetaccRole', None,varChild
			)
 def GetaccState(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5007, 2, (12, 0), ((12, 17),), 'GetaccState', None,varChild
			)
 def GetaccValue(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5004, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def Reset(self):

		return self._oleobj_.InvokeTypes(1610874905, LCID, 1, (24, 0), (),)
 def SetaccName(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5003, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def SetaccValue(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5004, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def ShowPopup(self, x=defaultNamedOptArg, y=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610874906, LCID, 1, (24, 0), ((12, 17), (12, 17)),x
			, y)
 def accChild(self, varChild=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(-5002, LCID, 2, (9, 0), ((12, 1),),varChild
			)

		if ret is not None:

			ret = Dispatch(ret, 'accChild', None, UnicodeToString=0)

		return ret
 def accDoDefaultAction(self, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5018, LCID, 1, (24, 0), ((12, 17),),varChild
			)
 def accHitTest(self, xLeft=defaultNamedNotOptArg, yTop=defaultNamedNotOptArg):

		return self._ApplyTypes_(-5017, 1, (12, 0), ((3, 1), (3, 1)), 'accHitTest', None,xLeft
			, yTop)
 def accLocation(self, pxLeft=pythoncom.Missing, pyTop=pythoncom.Missing, pcxWidth=pythoncom.Missing, pcyHeight=pythoncom.Missing
			, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5015, 1, (24, 0), ((16387, 2), (16387, 2), (16387, 2), (16387, 2), (12, 17)), 'accLocation', None,pxLeft
			, pyTop, pcxWidth, pcyHeight, varChild)
 def accNavigate(self, navDir=defaultNamedNotOptArg, varStart=defaultNamedOptArg):

		return self._ApplyTypes_(-5016, 1, (12, 0), ((3, 1), (12, 17)), 'accNavigate', None,navDir
			, varStart)
 def accSelect(self, flagsSelect=defaultNamedNotOptArg, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5014, LCID, 1, (24, 0), ((3, 1), (12, 17)),flagsSelect
			, varChild)

	_prop_map_get_ = {
		"AdaptiveMenu": (1610874914, 2, (11, 0), (), "AdaptiveMenu", None),
		"Application": (1610809344, 2, (9, 0), (), "Application", None),
		"BuiltIn": (1610874880, 2, (11, 0), (), "BuiltIn", None),
		"Context": (1610874881, 2, (8, 0), (), "Context", None),
		
		"Controls": (1610874883, 2, (9, 0), (), "Controls", '{000C0306-0000-0000-C000-000000000046}'),
		"Creator": (1610809345, 2, (3, 0), (), "Creator", None),
		"Enabled": (1610874885, 2, (11, 0), (), "Enabled", None),
		"Height": (1610874888, 2, (3, 0), (), "Height", None),
		"Index": (1610874890, 2, (3, 0), (), "Index", None),
		"InstanceId": (1610874891, 2, (3, 0), (), "InstanceId", None),
		"Left": (1610874892, 2, (3, 0), (), "Left", None),
		"Name": (1610874894, 2, (8, 0), (), "Name", None),
		"NameLocal": (1610874896, 2, (8, 0), (), "NameLocal", None),
		"Parent": (1610874898, 2, (9, 0), (), "Parent", None),
		"Position": (1610874899, 2, (3, 0), (), "Position", None),
		"Protection": (1610874903, 2, (3, 0), (), "Protection", None),
		"RowIndex": (1610874901, 2, (3, 0), (), "RowIndex", None),
		"Top": (1610874907, 2, (3, 0), (), "Top", None),
		"Type": (1610874909, 2, (3, 0), (), "Type", None),
		"Visible": (1610874910, 2, (11, 0), (), "Visible", None),
		"Width": (1610874912, 2, (3, 0), (), "Width", None),
		"accChildCount": (-5001, 2, (3, 0), (), "accChildCount", None),
		"accDefaultAction": (-5013, 2, (8, 0), ((12, 17),), "accDefaultAction", None),
		"accDescription": (-5005, 2, (8, 0), ((12, 17),), "accDescription", None),
		"accFocus": (-5011, 2, (12, 0), (), "accFocus", None),
		"accHelp": (-5008, 2, (8, 0), ((12, 17),), "accHelp", None),
		"accHelpTopic": (-5009, 2, (3, 0), ((16392, 2), (12, 17)), "accHelpTopic", None),
		"accKeyboardShortcut": (-5010, 2, (8, 0), ((12, 17),), "accKeyboardShortcut", None),
		"accName": (-5003, 2, (8, 0), ((12, 17),), "accName", None),
		"accParent": (-5000, 2, (9, 0), (), "accParent", None),
		"accRole": (-5006, 2, (12, 0), ((12, 17),), "accRole", None),
		"accSelection": (-5012, 2, (12, 0), (), "accSelection", None),
		"accState": (-5007, 2, (12, 0), ((12, 17),), "accState", None),
		"accValue": (-5004, 2, (8, 0), ((12, 17),), "accValue", None),
	}
		_prop_map_put_ = {
		"AdaptiveMenu": ((1610874914, LCID, 4, 0),()),
		"Context": ((1610874881, LCID, 4, 0),()),
		"Enabled": ((1610874885, LCID, 4, 0),()),
		"Height": ((1610874888, LCID, 4, 0),()),
		"Left": ((1610874892, LCID, 4, 0),()),
		"Name": ((1610874894, LCID, 4, 0),()),
		"NameLocal": ((1610874896, LCID, 4, 0),()),
		"Position": ((1610874899, LCID, 4, 0),()),
		"Protection": ((1610874903, LCID, 4, 0),()),
		"RowIndex": ((1610874901, LCID, 4, 0),()),
		"Top": ((1610874907, LCID, 4, 0),()),
		"Visible": ((1610874910, LCID, 4, 0),()),
		"Width": ((1610874912, LCID, 4, 0),()),
		"accName": ((-5003, LCID, 4, 0),()),
		"accValue": ((-5004, LCID, 4, 0),()),
	}
class  CommandBarControl (DispatchBaseClass) :
	CLSID = IID('{000C0308-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Copy(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874886, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Copy', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Delete(self, Temporary=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610874887, LCID, 1, (24, 0), ((12, 17),),Temporary
			)
 def Execute(self):

		return self._oleobj_.InvokeTypes(1610874892, LCID, 1, (24, 0), (),)
 def GetaccDefaultAction(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5013, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccDescription(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5005, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelp(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5008, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelpTopic(self, pszHelpFile=pythoncom.Missing, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5009, 2, (3, 0), ((16392, 2), (12, 17)), 'GetaccHelpTopic', None,pszHelpFile
			, varChild)
 def GetaccKeyboardShortcut(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5010, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccName(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5003, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccRole(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5006, 2, (12, 0), ((12, 17),), 'GetaccRole', None,varChild
			)
 def GetaccState(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5007, 2, (12, 0), ((12, 17),), 'GetaccState', None,varChild
			)
 def GetaccValue(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5004, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def Move(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874902, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Move', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Reserved1(self):

		return self._oleobj_.InvokeTypes(1610874926, LCID, 1, (24, 0), (),)
 def Reserved2(self):

		return self._oleobj_.InvokeTypes(1610874927, LCID, 1, (24, 0), (),)
 def Reserved3(self):

		return self._oleobj_.InvokeTypes(1610874928, LCID, 1, (24, 0), (),)
 def Reserved4(self):

		return self._oleobj_.InvokeTypes(1610874929, LCID, 1, (24, 0), (),)
 def Reserved5(self):

		return self._oleobj_.InvokeTypes(1610874930, LCID, 1, (24, 0), (),)
 def Reserved6(self):

		return self._oleobj_.InvokeTypes(1610874931, LCID, 1, (24, 0), (),)
 def Reserved7(self):

		return self._oleobj_.InvokeTypes(1610874932, LCID, 1, (24, 0), (),)
 def Reset(self):

		return self._oleobj_.InvokeTypes(1610874913, LCID, 1, (24, 0), (),)
 def SetFocus(self):

		return self._oleobj_.InvokeTypes(1610874914, LCID, 1, (24, 0), (),)
 def SetaccName(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5003, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def SetaccValue(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5004, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def accChild(self, varChild=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(-5002, LCID, 2, (9, 0), ((12, 1),),varChild
			)

		if ret is not None:

			ret = Dispatch(ret, 'accChild', None, UnicodeToString=0)

		return ret
 def accDoDefaultAction(self, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5018, LCID, 1, (24, 0), ((12, 17),),varChild
			)
 def accHitTest(self, xLeft=defaultNamedNotOptArg, yTop=defaultNamedNotOptArg):

		return self._ApplyTypes_(-5017, 1, (12, 0), ((3, 1), (3, 1)), 'accHitTest', None,xLeft
			, yTop)
 def accLocation(self, pxLeft=pythoncom.Missing, pyTop=pythoncom.Missing, pcxWidth=pythoncom.Missing, pcyHeight=pythoncom.Missing
			, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5015, 1, (24, 0), ((16387, 2), (16387, 2), (16387, 2), (16387, 2), (12, 17)), 'accLocation', None,pxLeft
			, pyTop, pcxWidth, pcyHeight, varChild)
 def accNavigate(self, navDir=defaultNamedNotOptArg, varStart=defaultNamedOptArg):

		return self._ApplyTypes_(-5016, 1, (12, 0), ((3, 1), (12, 17)), 'accNavigate', None,navDir
			, varStart)
 def accSelect(self, flagsSelect=defaultNamedNotOptArg, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5014, LCID, 1, (24, 0), ((3, 1), (12, 17)),flagsSelect
			, varChild)

	_prop_map_get_ = {
		"Application": (1610809344, 2, (9, 0), (), "Application", None),
		"BeginGroup": (1610874880, 2, (11, 0), (), "BeginGroup", None),
		"BuiltIn": (1610874882, 2, (11, 0), (), "BuiltIn", None),
		"Caption": (1610874883, 2, (8, 0), (), "Caption", None),
		"Control": (1610874885, 2, (9, 0), (), "Control", None),
		"Creator": (1610809345, 2, (3, 0), (), "Creator", None),
		"DescriptionText": (1610874888, 2, (8, 0), (), "DescriptionText", None),
		"Enabled": (1610874890, 2, (11, 0), (), "Enabled", None),
		"Height": (1610874893, 2, (3, 0), (), "Height", None),
		"HelpContextId": (1610874895, 2, (3, 0), (), "HelpContextId", None),
		"HelpFile": (1610874897, 2, (8, 0), (), "HelpFile", None),
		"Id": (1610874899, 2, (3, 0), (), "Id", None),
		"Index": (1610874900, 2, (3, 0), (), "Index", None),
		"InstanceId": (1610874901, 2, (3, 0), (), "InstanceId", None),
		"IsPriorityDropped": (1610874925, 2, (11, 0), (), "IsPriorityDropped", None),
		"Left": (1610874903, 2, (3, 0), (), "Left", None),
		"OLEUsage": (1610874904, 2, (3, 0), (), "OLEUsage", None),
		"OnAction": (1610874906, 2, (8, 0), (), "OnAction", None),
		"Parameter": (1610874909, 2, (8, 0), (), "Parameter", None),
		
		"Parent": (1610874908, 2, (9, 0), (), "Parent", '{000C0304-0000-0000-C000-000000000046}'),
		"Priority": (1610874911, 2, (3, 0), (), "Priority", None),
		"Tag": (1610874915, 2, (8, 0), (), "Tag", None),
		"TooltipText": (1610874917, 2, (8, 0), (), "TooltipText", None),
		"Top": (1610874919, 2, (3, 0), (), "Top", None),
		"Type": (1610874920, 2, (3, 0), (), "Type", None),
		"Visible": (1610874921, 2, (11, 0), (), "Visible", None),
		"Width": (1610874923, 2, (3, 0), (), "Width", None),
		"accChildCount": (-5001, 2, (3, 0), (), "accChildCount", None),
		"accDefaultAction": (-5013, 2, (8, 0), ((12, 17),), "accDefaultAction", None),
		"accDescription": (-5005, 2, (8, 0), ((12, 17),), "accDescription", None),
		"accFocus": (-5011, 2, (12, 0), (), "accFocus", None),
		"accHelp": (-5008, 2, (8, 0), ((12, 17),), "accHelp", None),
		"accHelpTopic": (-5009, 2, (3, 0), ((16392, 2), (12, 17)), "accHelpTopic", None),
		"accKeyboardShortcut": (-5010, 2, (8, 0), ((12, 17),), "accKeyboardShortcut", None),
		"accName": (-5003, 2, (8, 0), ((12, 17),), "accName", None),
		"accParent": (-5000, 2, (9, 0), (), "accParent", None),
		"accRole": (-5006, 2, (12, 0), ((12, 17),), "accRole", None),
		"accSelection": (-5012, 2, (12, 0), (), "accSelection", None),
		"accState": (-5007, 2, (12, 0), ((12, 17),), "accState", None),
		"accValue": (-5004, 2, (8, 0), ((12, 17),), "accValue", None),
	}
		_prop_map_put_ = {
		"BeginGroup": ((1610874880, LCID, 4, 0),()),
		"Caption": ((1610874883, LCID, 4, 0),()),
		"DescriptionText": ((1610874888, LCID, 4, 0),()),
		"Enabled": ((1610874890, LCID, 4, 0),()),
		"Height": ((1610874893, LCID, 4, 0),()),
		"HelpContextId": ((1610874895, LCID, 4, 0),()),
		"HelpFile": ((1610874897, LCID, 4, 0),()),
		"OLEUsage": ((1610874904, LCID, 4, 0),()),
		"OnAction": ((1610874906, LCID, 4, 0),()),
		"Parameter": ((1610874909, LCID, 4, 0),()),
		"Priority": ((1610874911, LCID, 4, 0),()),
		"Tag": ((1610874915, LCID, 4, 0),()),
		"TooltipText": ((1610874917, LCID, 4, 0),()),
		"Visible": ((1610874921, LCID, 4, 0),()),
		"Width": ((1610874923, LCID, 4, 0),()),
		"accName": ((-5003, LCID, 4, 0),()),
		"accValue": ((-5004, LCID, 4, 0),()),
	}
class  CommandBarControls (DispatchBaseClass) :
	CLSID = IID('{000C0306-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Add(self, Type=defaultNamedOptArg, Id=defaultNamedOptArg, Parameter=defaultNamedOptArg, Before=defaultNamedOptArg
			, Temporary=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610809344, LCID, 1, (9, 0), ((12, 17), (12, 17), (12, 17), (12, 17), (12, 17)),Type
			, Id, Parameter, Before, Temporary)

		if ret is not None:

			ret = Dispatch(ret, 'Add', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (1610809345, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		
		"Parent": (1610809348, 2, (9, 0), (), "Parent", '{000C0304-0000-0000-C000-000000000046}'),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C0308-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(1610809345, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  CommandBarPopup (DispatchBaseClass) :
	CLSID = IID('{000C030A-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Copy(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874886, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Copy', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Delete(self, Temporary=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610874887, LCID, 1, (24, 0), ((12, 17),),Temporary
			)
 def Execute(self):

		return self._oleobj_.InvokeTypes(1610874892, LCID, 1, (24, 0), (),)
 def GetaccDefaultAction(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5013, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccDescription(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5005, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelp(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5008, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelpTopic(self, pszHelpFile=pythoncom.Missing, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5009, 2, (3, 0), ((16392, 2), (12, 17)), 'GetaccHelpTopic', None,pszHelpFile
			, varChild)
 def GetaccKeyboardShortcut(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5010, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccName(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5003, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccRole(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5006, 2, (12, 0), ((12, 17),), 'GetaccRole', None,varChild
			)
 def GetaccState(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5007, 2, (12, 0), ((12, 17),), 'GetaccState', None,varChild
			)
 def GetaccValue(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5004, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def Move(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874902, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Move', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Reserved1(self):

		return self._oleobj_.InvokeTypes(1610874926, LCID, 1, (24, 0), (),)
 def Reserved2(self):

		return self._oleobj_.InvokeTypes(1610874927, LCID, 1, (24, 0), (),)
 def Reserved3(self):

		return self._oleobj_.InvokeTypes(1610874928, LCID, 1, (24, 0), (),)
 def Reserved4(self):

		return self._oleobj_.InvokeTypes(1610874929, LCID, 1, (24, 0), (),)
 def Reserved5(self):

		return self._oleobj_.InvokeTypes(1610874930, LCID, 1, (24, 0), (),)
 def Reserved6(self):

		return self._oleobj_.InvokeTypes(1610874931, LCID, 1, (24, 0), (),)
 def Reserved7(self):

		return self._oleobj_.InvokeTypes(1610874932, LCID, 1, (24, 0), (),)
 def Reset(self):

		return self._oleobj_.InvokeTypes(1610874913, LCID, 1, (24, 0), (),)
 def SetFocus(self):

		return self._oleobj_.InvokeTypes(1610874914, LCID, 1, (24, 0), (),)
 def SetaccName(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5003, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def SetaccValue(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5004, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def accChild(self, varChild=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(-5002, LCID, 2, (9, 0), ((12, 1),),varChild
			)

		if ret is not None:

			ret = Dispatch(ret, 'accChild', None, UnicodeToString=0)

		return ret
 def accDoDefaultAction(self, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5018, LCID, 1, (24, 0), ((12, 17),),varChild
			)
 def accHitTest(self, xLeft=defaultNamedNotOptArg, yTop=defaultNamedNotOptArg):

		return self._ApplyTypes_(-5017, 1, (12, 0), ((3, 1), (3, 1)), 'accHitTest', None,xLeft
			, yTop)
 def accLocation(self, pxLeft=pythoncom.Missing, pyTop=pythoncom.Missing, pcxWidth=pythoncom.Missing, pcyHeight=pythoncom.Missing
			, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5015, 1, (24, 0), ((16387, 2), (16387, 2), (16387, 2), (16387, 2), (12, 17)), 'accLocation', None,pxLeft
			, pyTop, pcxWidth, pcyHeight, varChild)
 def accNavigate(self, navDir=defaultNamedNotOptArg, varStart=defaultNamedOptArg):

		return self._ApplyTypes_(-5016, 1, (12, 0), ((3, 1), (12, 17)), 'accNavigate', None,navDir
			, varStart)
 def accSelect(self, flagsSelect=defaultNamedNotOptArg, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5014, LCID, 1, (24, 0), ((3, 1), (12, 17)),flagsSelect
			, varChild)

	_prop_map_get_ = {
		"Application": (1610809344, 2, (9, 0), (), "Application", None),
		"BeginGroup": (1610874880, 2, (11, 0), (), "BeginGroup", None),
		"BuiltIn": (1610874882, 2, (11, 0), (), "BuiltIn", None),
		"Caption": (1610874883, 2, (8, 0), (), "Caption", None),
		
		"CommandBar": (1610940416, 2, (9, 0), (), "CommandBar", '{000C0304-0000-0000-C000-000000000046}'),
		"Control": (1610874885, 2, (9, 0), (), "Control", None),
		
		"Controls": (1610940417, 2, (9, 0), (), "Controls", '{000C0306-0000-0000-C000-000000000046}'),
		"Creator": (1610809345, 2, (3, 0), (), "Creator", None),
		"DescriptionText": (1610874888, 2, (8, 0), (), "DescriptionText", None),
		"Enabled": (1610874890, 2, (11, 0), (), "Enabled", None),
		"Height": (1610874893, 2, (3, 0), (), "Height", None),
		"HelpContextId": (1610874895, 2, (3, 0), (), "HelpContextId", None),
		"HelpFile": (1610874897, 2, (8, 0), (), "HelpFile", None),
		"Id": (1610874899, 2, (3, 0), (), "Id", None),
		"Index": (1610874900, 2, (3, 0), (), "Index", None),
		"InstanceId": (1610874901, 2, (3, 0), (), "InstanceId", None),
		"IsPriorityDropped": (1610874925, 2, (11, 0), (), "IsPriorityDropped", None),
		"Left": (1610874903, 2, (3, 0), (), "Left", None),
		"OLEMenuGroup": (1610940418, 2, (3, 0), (), "OLEMenuGroup", None),
		"OLEUsage": (1610874904, 2, (3, 0), (), "OLEUsage", None),
		"OnAction": (1610874906, 2, (8, 0), (), "OnAction", None),
		"Parameter": (1610874909, 2, (8, 0), (), "Parameter", None),
		
		"Parent": (1610874908, 2, (9, 0), (), "Parent", '{000C0304-0000-0000-C000-000000000046}'),
		"Priority": (1610874911, 2, (3, 0), (), "Priority", None),
		"Tag": (1610874915, 2, (8, 0), (), "Tag", None),
		"TooltipText": (1610874917, 2, (8, 0), (), "TooltipText", None),
		"Top": (1610874919, 2, (3, 0), (), "Top", None),
		"Type": (1610874920, 2, (3, 0), (), "Type", None),
		"Visible": (1610874921, 2, (11, 0), (), "Visible", None),
		"Width": (1610874923, 2, (3, 0), (), "Width", None),
		"accChildCount": (-5001, 2, (3, 0), (), "accChildCount", None),
		"accDefaultAction": (-5013, 2, (8, 0), ((12, 17),), "accDefaultAction", None),
		"accDescription": (-5005, 2, (8, 0), ((12, 17),), "accDescription", None),
		"accFocus": (-5011, 2, (12, 0), (), "accFocus", None),
		"accHelp": (-5008, 2, (8, 0), ((12, 17),), "accHelp", None),
		"accHelpTopic": (-5009, 2, (3, 0), ((16392, 2), (12, 17)), "accHelpTopic", None),
		"accKeyboardShortcut": (-5010, 2, (8, 0), ((12, 17),), "accKeyboardShortcut", None),
		"accName": (-5003, 2, (8, 0), ((12, 17),), "accName", None),
		"accParent": (-5000, 2, (9, 0), (), "accParent", None),
		"accRole": (-5006, 2, (12, 0), ((12, 17),), "accRole", None),
		"accSelection": (-5012, 2, (12, 0), (), "accSelection", None),
		"accState": (-5007, 2, (12, 0), ((12, 17),), "accState", None),
		"accValue": (-5004, 2, (8, 0), ((12, 17),), "accValue", None),
	}
		_prop_map_put_ = {
		"BeginGroup": ((1610874880, LCID, 4, 0),()),
		"Caption": ((1610874883, LCID, 4, 0),()),
		"DescriptionText": ((1610874888, LCID, 4, 0),()),
		"Enabled": ((1610874890, LCID, 4, 0),()),
		"Height": ((1610874893, LCID, 4, 0),()),
		"HelpContextId": ((1610874895, LCID, 4, 0),()),
		"HelpFile": ((1610874897, LCID, 4, 0),()),
		"OLEMenuGroup": ((1610940418, LCID, 4, 0),()),
		"OLEUsage": ((1610874904, LCID, 4, 0),()),
		"OnAction": ((1610874906, LCID, 4, 0),()),
		"Parameter": ((1610874909, LCID, 4, 0),()),
		"Priority": ((1610874911, LCID, 4, 0),()),
		"Tag": ((1610874915, LCID, 4, 0),()),
		"TooltipText": ((1610874917, LCID, 4, 0),()),
		"Visible": ((1610874921, LCID, 4, 0),()),
		"Width": ((1610874923, LCID, 4, 0),()),
		"accName": ((-5003, LCID, 4, 0),()),
		"accValue": ((-5004, LCID, 4, 0),()),
	}
class  ConnectorFormat (DispatchBaseClass) :
	CLSID = IID('{000C0313-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def BeginConnect(self, ConnectedShape=defaultNamedNotOptArg, ConnectionSite=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), ((9, 1), (3, 1)),ConnectedShape
			, ConnectionSite)
 def BeginDisconnect(self):

		return self._oleobj_.InvokeTypes(11, LCID, 1, (24, 0), (),)
 def EndConnect(self, ConnectedShape=defaultNamedNotOptArg, ConnectionSite=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(12, LCID, 1, (24, 0), ((9, 1), (3, 1)),ConnectedShape
			, ConnectionSite)
 def EndDisconnect(self):

		return self._oleobj_.InvokeTypes(13, LCID, 1, (24, 0), (),)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"BeginConnected": (100, 2, (3, 0), (), "BeginConnected", None),
		
		"BeginConnectedShape": (101, 2, (9, 0), (), "BeginConnectedShape", '{000C031C-0000-0000-C000-000000000046}'),
		"BeginConnectionSite": (102, 2, (3, 0), (), "BeginConnectionSite", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"EndConnected": (103, 2, (3, 0), (), "EndConnected", None),
		
		"EndConnectedShape": (104, 2, (9, 0), (), "EndConnectedShape", '{000C031C-0000-0000-C000-000000000046}'),
		"EndConnectionSite": (105, 2, (3, 0), (), "EndConnectionSite", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"Type": (106, 2, (3, 0), (), "Type", None),
	}
		_prop_map_put_ = {
		"Type": ((106, LCID, 4, 0),()),
	}
class  DocumentProperties (DispatchBaseClass) :
	CLSID = IID('{2DF8D04D-5BFA-101B-BDE5-00AA0044DE52}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743813, 2, (3, 0), ((16393, 10),), "Application", None),
		"Count": (4, 2, (3, 0), ((16387, 10),), "Count", None),
		"Creator": (1610743814, 2, (3, 0), ((16387, 10),), "Creator", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
		def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),None)
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(4, 2, (3, 0), ((16387, 10),), "Count", None))
 def __nonzero__(self):

		return True

class  DocumentProperty (DispatchBaseClass) :
	CLSID = IID('{2DF8D04E-5BFA-101B-BDE5-00AA0044DE52}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743820, 2, (3, 0), ((16393, 10),), "Application", None),
		"Creator": (1610743821, 2, (3, 0), ((16387, 10),), "Creator", None),
		"LinkSource": (7, 2, (3, 0), ((16392, 10),), "LinkSource", None),
		"LinkToContent": (6, 2, (3, 0), ((16395, 10),), "LinkToContent", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
		"LinkSource": ((7, LCID, 4, 0),()),
		"LinkToContent": ((6, LCID, 4, 0),()),
	}
class  FileSearch (DispatchBaseClass) :
	CLSID = IID('{000C0332-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Execute(self, SortBy=1, SortOrder=1, AlwaysAccurate=True):

		return self._oleobj_.InvokeTypes(9, LCID, 1, (3, 0), ((3, 49), (3, 49), (11, 49)),SortBy
			, SortOrder, AlwaysAccurate)
 def NewSearch(self):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), (),)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"FileName": (4, 2, (8, 0), (), "FileName", None),
		"FileType": (5, 2, (3, 0), (), "FileType", None),
		
		"FoundFiles": (11, 2, (9, 0), (), "FoundFiles", '{000C0331-0000-0000-C000-000000000046}'),
		"LastModified": (6, 2, (3, 0), (), "LastModified", None),
		"LookIn": (8, 2, (8, 0), (), "LookIn", None),
		"MatchAllWordForms": (3, 2, (11, 0), (), "MatchAllWordForms", None),
		"MatchTextExactly": (2, 2, (11, 0), (), "MatchTextExactly", None),
		
		"PropertyTests": (12, 2, (9, 0), (), "PropertyTests", '{000C0334-0000-0000-C000-000000000046}'),
		"SearchSubFolders": (1, 2, (11, 0), (), "SearchSubFolders", None),
		"TextOrProperty": (7, 2, (8, 0), (), "TextOrProperty", None),
	}
		_prop_map_put_ = {
		"FileName": ((4, LCID, 4, 0),()),
		"FileType": ((5, LCID, 4, 0),()),
		"LastModified": ((6, LCID, 4, 0),()),
		"LookIn": ((8, LCID, 4, 0),()),
		"MatchAllWordForms": ((3, LCID, 4, 0),()),
		"MatchTextExactly": ((2, LCID, 4, 0),()),
		"SearchSubFolders": ((1, LCID, 4, 0),()),
		"TextOrProperty": ((7, LCID, 4, 0),()),
	}
class  FillFormat (DispatchBaseClass) :
	CLSID = IID('{000C0314-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Background(self):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), (),)
 def OneColorGradient(self, Style=defaultNamedNotOptArg, Variant=defaultNamedNotOptArg, Degree=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(11, LCID, 1, (24, 0), ((3, 1), (3, 1), (4, 1)),Style
			, Variant, Degree)
 def Patterned(self, Pattern=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(12, LCID, 1, (24, 0), ((3, 1),),Pattern
			)
 def PresetGradient(self, Style=defaultNamedNotOptArg, Variant=defaultNamedNotOptArg, PresetGradientType=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(13, LCID, 1, (24, 0), ((3, 1), (3, 1), (3, 1)),Style
			, Variant, PresetGradientType)
 def PresetTextured(self, PresetTexture=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(14, LCID, 1, (24, 0), ((3, 1),),PresetTexture
			)
 def Solid(self):

		return self._oleobj_.InvokeTypes(15, LCID, 1, (24, 0), (),)
 def TwoColorGradient(self, Style=defaultNamedNotOptArg, Variant=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(16, LCID, 1, (24, 0), ((3, 1), (3, 1)),Style
			, Variant)
 def UserPicture(self, PictureFile=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(17, LCID, 1, (24, 0), ((8, 1),),PictureFile
			)
 def UserTextured(self, TextureFile=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(18, LCID, 1, (24, 0), ((8, 1),),TextureFile
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		
		"BackColor": (100, 2, (9, 0), (), "BackColor", '{000C0312-0000-0000-C000-000000000046}'),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		
		"ForeColor": (101, 2, (9, 0), (), "ForeColor", '{000C0312-0000-0000-C000-000000000046}'),
		"GradientColorType": (102, 2, (3, 0), (), "GradientColorType", None),
		"GradientDegree": (103, 2, (4, 0), (), "GradientDegree", None),
		"GradientStyle": (104, 2, (3, 0), (), "GradientStyle", None),
		"GradientVariant": (105, 2, (3, 0), (), "GradientVariant", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"Pattern": (106, 2, (3, 0), (), "Pattern", None),
		"PresetGradientType": (107, 2, (3, 0), (), "PresetGradientType", None),
		"PresetTexture": (108, 2, (3, 0), (), "PresetTexture", None),
		"TextureName": (109, 2, (8, 0), (), "TextureName", None),
		"TextureType": (110, 2, (3, 0), (), "TextureType", None),
		"Transparency": (111, 2, (4, 0), (), "Transparency", None),
		"Type": (112, 2, (3, 0), (), "Type", None),
		"Visible": (113, 2, (3, 0), (), "Visible", None),
	}
		_prop_map_put_ = {
		"BackColor": ((100, LCID, 4, 0),()),
		"ForeColor": ((101, LCID, 4, 0),()),
		"Transparency": ((111, LCID, 4, 0),()),
		"Visible": ((113, LCID, 4, 0),()),
	}
class  FoundFiles (DispatchBaseClass) :
	CLSID = IID('{000C0331-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Item(self, Index=defaultNamedNotOptArg):

		
		return self._oleobj_.InvokeTypes(0, LCID, 2, (8, 0), ((3, 1),),Index
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (4, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		
		return self._oleobj_.InvokeTypes(0, LCID, 2, (8, 0), ((3, 1),),Index
			)
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),None)
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(4, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  FreeformBuilder (DispatchBaseClass) :
	CLSID = IID('{000C0315-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def AddNodes(self, SegmentType=defaultNamedNotOptArg, EditingType=defaultNamedNotOptArg, X1=defaultNamedNotOptArg, Y1=defaultNamedNotOptArg
			, X2=0.0, Y2=0.0, X3=0.0, Y3=0.0):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), ((3, 1), (3, 1), (4, 1), (4, 1), (4, 49), (4, 49), (4, 49), (4, 49)),SegmentType
			, EditingType, X1, Y1, X2, Y2
			, X3, Y3)
 def ConvertToShape(self):

		ret = self._oleobj_.InvokeTypes(11, LCID, 1, (9, 0), (),)

		if ret is not None:

			ret = Dispatch(ret, 'ConvertToShape', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
class  GroupShapes (DispatchBaseClass) :
	CLSID = IID('{000C0316-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (2, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C031C-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(2, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  HTMLProject (DispatchBaseClass) :
	CLSID = IID('{000C0356-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Open(self, OpenKind=0):

		return self._oleobj_.InvokeTypes(5, LCID, 1, (24, 0), ((3, 49),),OpenKind
			)
 def RefreshDocument(self, Refresh=True):

		return self._oleobj_.InvokeTypes(2, LCID, 1, (24, 0), ((11, 49),),Refresh
			)
 def RefreshProject(self, Refresh=True):

		return self._oleobj_.InvokeTypes(1, LCID, 1, (24, 0), ((11, 49),),Refresh
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		
		"HTMLProjectItems": (3, 2, (9, 0), (), "HTMLProjectItems", '{000C0357-0000-0000-C000-000000000046}'),
		"Parent": (4, 2, (9, 0), (), "Parent", None),
		"State": (0, 2, (3, 0), (), "State", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (3, 0), (), "State", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  HTMLProjectItem (DispatchBaseClass) :
	CLSID = IID('{000C0358-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def LoadFromFile(self, FileName=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(5, LCID, 1, (24, 0), ((8, 1),),FileName
			)
 def Open(self, OpenKind=0):

		return self._oleobj_.InvokeTypes(6, LCID, 1, (24, 0), ((3, 49),),OpenKind
			)
 def SaveCopyAs(self, FileName=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(7, LCID, 1, (24, 0), ((8, 1),),FileName
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"IsOpen": (4, 2, (11, 0), (), "IsOpen", None),
		"Name": (0, 2, (8, 0), (), "Name", None),
		"Parent": (10, 2, (9, 0), (), "Parent", None),
		"Text": (8, 2, (8, 0), (), "Text", None),
	}
		_prop_map_put_ = {
		"Text": ((8, LCID, 4, 0),()),
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (8, 0), (), "Name", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  HTMLProjectItems (DispatchBaseClass) :
	CLSID = IID('{000C0357-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((16396, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C0358-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (1, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Parent": (2, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((16396, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C0358-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C0358-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(1, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  IAccessible (DispatchBaseClass) :
	CLSID = IID('{618736E0-3C3D-11CF-810C-00AA00389B71}')
		coclass_clsid = None
		
	def GetaccDefaultAction(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5013, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccDescription(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5005, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelp(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5008, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelpTopic(self, pszHelpFile=pythoncom.Missing, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5009, 2, (3, 0), ((16392, 2), (12, 17)), 'GetaccHelpTopic', None,pszHelpFile
			, varChild)
 def GetaccKeyboardShortcut(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5010, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccName(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5003, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccRole(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5006, 2, (12, 0), ((12, 17),), 'GetaccRole', None,varChild
			)
 def GetaccState(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5007, 2, (12, 0), ((12, 17),), 'GetaccState', None,varChild
			)
 def GetaccValue(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5004, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def SetaccName(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5003, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def SetaccValue(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5004, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def accChild(self, varChild=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(-5002, LCID, 2, (9, 0), ((12, 1),),varChild
			)

		if ret is not None:

			ret = Dispatch(ret, 'accChild', None, UnicodeToString=0)

		return ret
 def accDoDefaultAction(self, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5018, LCID, 1, (24, 0), ((12, 17),),varChild
			)
 def accHitTest(self, xLeft=defaultNamedNotOptArg, yTop=defaultNamedNotOptArg):

		return self._ApplyTypes_(-5017, 1, (12, 0), ((3, 1), (3, 1)), 'accHitTest', None,xLeft
			, yTop)
 def accLocation(self, pxLeft=pythoncom.Missing, pyTop=pythoncom.Missing, pcxWidth=pythoncom.Missing, pcyHeight=pythoncom.Missing
			, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5015, 1, (24, 0), ((16387, 2), (16387, 2), (16387, 2), (16387, 2), (12, 17)), 'accLocation', None,pxLeft
			, pyTop, pcxWidth, pcyHeight, varChild)
 def accNavigate(self, navDir=defaultNamedNotOptArg, varStart=defaultNamedOptArg):

		return self._ApplyTypes_(-5016, 1, (12, 0), ((3, 1), (12, 17)), 'accNavigate', None,navDir
			, varStart)
 def accSelect(self, flagsSelect=defaultNamedNotOptArg, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5014, LCID, 1, (24, 0), ((3, 1), (12, 17)),flagsSelect
			, varChild)

	_prop_map_get_ = {
		"accChildCount": (-5001, 2, (3, 0), (), "accChildCount", None),
		"accDefaultAction": (-5013, 2, (8, 0), ((12, 17),), "accDefaultAction", None),
		"accDescription": (-5005, 2, (8, 0), ((12, 17),), "accDescription", None),
		"accFocus": (-5011, 2, (12, 0), (), "accFocus", None),
		"accHelp": (-5008, 2, (8, 0), ((12, 17),), "accHelp", None),
		"accHelpTopic": (-5009, 2, (3, 0), ((16392, 2), (12, 17)), "accHelpTopic", None),
		"accKeyboardShortcut": (-5010, 2, (8, 0), ((12, 17),), "accKeyboardShortcut", None),
		"accName": (-5003, 2, (8, 0), ((12, 17),), "accName", None),
		"accParent": (-5000, 2, (9, 0), (), "accParent", None),
		"accRole": (-5006, 2, (12, 0), ((12, 17),), "accRole", None),
		"accSelection": (-5012, 2, (12, 0), (), "accSelection", None),
		"accState": (-5007, 2, (12, 0), ((12, 17),), "accState", None),
		"accValue": (-5004, 2, (8, 0), ((12, 17),), "accValue", None),
	}
		_prop_map_put_ = {
		"accName": ((-5003, LCID, 4, 0),()),
		"accValue": ((-5004, LCID, 4, 0),()),
	}
class  ICommandBarButtonEvents (DispatchBaseClass) :
	CLSID = IID('{55F88890-7708-11D1-ACEB-006008961DA5}')
		coclass_clsid = None
		def Click(self, Ctrl=defaultNamedNotOptArg, CancelDefault=defaultNamedNotOptArg):

		return self._ApplyTypes_(1, 1, (24, 0), ((13, 1), (16395, 3)), 'Click', None,Ctrl
			, CancelDefault)

	_prop_map_get_ = {
	}
		_prop_map_put_ = {
	}
class  ICommandBarComboBoxEvents (DispatchBaseClass) :
	CLSID = IID('{55F88896-7708-11D1-ACEB-006008961DA5}')
		coclass_clsid = None
		def Change(self, Ctrl=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1, LCID, 1, (24, 0), ((13, 1),),Ctrl
			)

	_prop_map_get_ = {
	}
		_prop_map_put_ = {
	}
class  ICommandBarsEvents (DispatchBaseClass) :
	CLSID = IID('{55F88892-7708-11D1-ACEB-006008961DA5}')
		coclass_clsid = None
		def OnUpdate(self):

		return self._oleobj_.InvokeTypes(1, LCID, 1, (24, 0), (),)

	_prop_map_get_ = {
	}
		_prop_map_put_ = {
	}
class  IFind (DispatchBaseClass) :
	CLSID = IID('{000C0337-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Delete(self, bstrQueryName=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1610743853, LCID, 1, (24, 0), ((8, 1),),bstrQueryName
			)
 def Execute(self):

		return self._oleobj_.InvokeTypes(1610743850, LCID, 1, (24, 0), (),)
 def Load(self, bstrQueryName=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1610743851, LCID, 1, (24, 0), ((8, 1),),bstrQueryName
			)
 def Save(self, bstrQueryName=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1610743852, LCID, 1, (24, 0), ((8, 1),),bstrQueryName
			)
 def Show(self):

		return self._oleobj_.InvokeTypes(1610743829, LCID, 1, (3, 0), (),)

	_prop_map_get_ = {
		"Author": (1610743812, 2, (8, 0), (), "Author", None),
		"DateCreatedFrom": (1610743822, 2, (12, 0), (), "DateCreatedFrom", None),
		"DateCreatedTo": (1610743823, 2, (12, 0), (), "DateCreatedTo", None),
		"DateSavedFrom": (1610743819, 2, (12, 0), (), "DateSavedFrom", None),
		"DateSavedTo": (1610743820, 2, (12, 0), (), "DateSavedTo", None),
		"FileType": (1610743854, 2, (3, 0), (), "FileType", None),
		"Keywords": (1610743813, 2, (8, 0), (), "Keywords", None),
		"ListBy": (1610743826, 2, (3, 0), (), "ListBy", None),
		"MatchCase": (1610743816, 2, (11, 0), (), "MatchCase", None),
		"Name": (1610743809, 2, (8, 0), (), "Name", None),
		"Options": (1610743815, 2, (3, 0), (), "Options", None),
		"PatternMatch": (1610743818, 2, (11, 0), (), "PatternMatch", None),
		
		"Results": (1610743828, 2, (9, 0), (), "Results", '{000C0338-0000-0000-C000-000000000046}'),
		"SavedBy": (1610743821, 2, (8, 0), (), "SavedBy", None),
		"SearchPath": (0, 2, (8, 0), (), "SearchPath", None),
		"SelectedFile": (1610743827, 2, (3, 0), (), "SelectedFile", None),
		"SortBy": (1610743825, 2, (3, 0), (), "SortBy", None),
		"SubDir": (1610743810, 2, (11, 0), (), "SubDir", None),
		"Subject": (1610743814, 2, (8, 0), (), "Subject", None),
		"Text": (1610743817, 2, (8, 0), (), "Text", None),
		"Title": (1610743811, 2, (8, 0), (), "Title", None),
		"View": (1610743824, 2, (3, 0), (), "View", None),
	}
		_prop_map_put_ = {
		"Author": ((1610743812, LCID, 4, 0),()),
		"DateCreatedFrom": ((1610743822, LCID, 4, 0),()),
		"DateCreatedTo": ((1610743823, LCID, 4, 0),()),
		"DateSavedFrom": ((1610743819, LCID, 4, 0),()),
		"DateSavedTo": ((1610743820, LCID, 4, 0),()),
		"FileType": ((1610743854, LCID, 4, 0),()),
		"Keywords": ((1610743813, LCID, 4, 0),()),
		"ListBy": ((1610743826, LCID, 4, 0),()),
		"MatchCase": ((1610743816, LCID, 4, 0),()),
		"Name": ((1610743809, LCID, 4, 0),()),
		"Options": ((1610743815, LCID, 4, 0),()),
		"PatternMatch": ((1610743818, LCID, 4, 0),()),
		"SavedBy": ((1610743821, LCID, 4, 0),()),
		"SearchPath": ((0, LCID, 4, 0),()),
		"SelectedFile": ((1610743827, LCID, 4, 0),()),
		"SortBy": ((1610743825, LCID, 4, 0),()),
		"SubDir": ((1610743810, LCID, 4, 0),()),
		"Subject": ((1610743814, LCID, 4, 0),()),
		"Text": ((1610743817, LCID, 4, 0),()),
		"Title": ((1610743811, LCID, 4, 0),()),
		"View": ((1610743824, LCID, 4, 0),()),
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (8, 0), (), "SearchPath", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  IFoundFiles (DispatchBaseClass) :
	CLSID = IID('{000C0338-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Item(self, Index=defaultNamedNotOptArg):

		
		return self._oleobj_.InvokeTypes(0, LCID, 2, (8, 0), ((3, 1),),Index
			)

	_prop_map_get_ = {
		"Count": (1610743809, 2, (3, 0), (), "Count", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		
		return self._oleobj_.InvokeTypes(0, LCID, 2, (8, 0), ((3, 1),),Index
			)
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),None)
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(1610743809, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  IMsoDispCagNotifySink (DispatchBaseClass) :
	CLSID = IID('{000C0359-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def InsertClip(self, pClipMoniker=defaultNamedNotOptArg, pItemMoniker=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1, LCID, 1, (24, 0), ((13, 1), (13, 1)),pClipMoniker
			, pItemMoniker)
 def WindowIsClosing(self):

		return self._oleobj_.InvokeTypes(2, LCID, 1, (24, 0), (),)

	_prop_map_get_ = {
	}
		_prop_map_put_ = {
	}
class  LanguageSettings (DispatchBaseClass) :
	CLSID = IID('{000C0353-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def LanguageID(self, Id=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1, LCID, 2, (3, 0), ((3, 1),),Id
			)
 def LanguagePreferredForEditing(self, lid=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(2, LCID, 2, (11, 0), ((3, 1),),lid
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
	}
		_prop_map_put_ = {
	}
class  LineFormat (DispatchBaseClass) :
	CLSID = IID('{000C0317-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		
		"BackColor": (100, 2, (9, 0), (), "BackColor", '{000C0312-0000-0000-C000-000000000046}'),
		"BeginArrowheadLength": (101, 2, (3, 0), (), "BeginArrowheadLength", None),
		"BeginArrowheadStyle": (102, 2, (3, 0), (), "BeginArrowheadStyle", None),
		"BeginArrowheadWidth": (103, 2, (3, 0), (), "BeginArrowheadWidth", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"DashStyle": (104, 2, (3, 0), (), "DashStyle", None),
		"EndArrowheadLength": (105, 2, (3, 0), (), "EndArrowheadLength", None),
		"EndArrowheadStyle": (106, 2, (3, 0), (), "EndArrowheadStyle", None),
		"EndArrowheadWidth": (107, 2, (3, 0), (), "EndArrowheadWidth", None),
		
		"ForeColor": (108, 2, (9, 0), (), "ForeColor", '{000C0312-0000-0000-C000-000000000046}'),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"Pattern": (109, 2, (3, 0), (), "Pattern", None),
		"Style": (110, 2, (3, 0), (), "Style", None),
		"Transparency": (111, 2, (4, 0), (), "Transparency", None),
		"Visible": (112, 2, (3, 0), (), "Visible", None),
		"Weight": (113, 2, (4, 0), (), "Weight", None),
	}
		_prop_map_put_ = {
		"BackColor": ((100, LCID, 4, 0),()),
		"BeginArrowheadLength": ((101, LCID, 4, 0),()),
		"BeginArrowheadStyle": ((102, LCID, 4, 0),()),
		"BeginArrowheadWidth": ((103, LCID, 4, 0),()),
		"DashStyle": ((104, LCID, 4, 0),()),
		"EndArrowheadLength": ((105, LCID, 4, 0),()),
		"EndArrowheadStyle": ((106, LCID, 4, 0),()),
		"EndArrowheadWidth": ((107, LCID, 4, 0),()),
		"ForeColor": ((108, LCID, 4, 0),()),
		"Pattern": ((109, LCID, 4, 0),()),
		"Style": ((110, LCID, 4, 0),()),
		"Transparency": ((111, LCID, 4, 0),()),
		"Visible": ((112, LCID, 4, 0),()),
		"Weight": ((113, LCID, 4, 0),()),
	}
class  MsoDebugOptions (DispatchBaseClass) :
	CLSID = IID('{000C035A-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"FeatureReports": (4, 2, (3, 0), (), "FeatureReports", None),
	}
		_prop_map_put_ = {
		"FeatureReports": ((4, LCID, 4, 0),()),
	}
class  PictureFormat (DispatchBaseClass) :
	CLSID = IID('{000C031A-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def IncrementBrightness(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def IncrementContrast(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(11, LCID, 1, (24, 0), ((4, 1),),Increment
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Brightness": (100, 2, (4, 0), (), "Brightness", None),
		"ColorType": (101, 2, (3, 0), (), "ColorType", None),
		"Contrast": (102, 2, (4, 0), (), "Contrast", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"CropBottom": (103, 2, (4, 0), (), "CropBottom", None),
		"CropLeft": (104, 2, (4, 0), (), "CropLeft", None),
		"CropRight": (105, 2, (4, 0), (), "CropRight", None),
		"CropTop": (106, 2, (4, 0), (), "CropTop", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"TransparencyColor": (107, 2, (3, 0), (), "TransparencyColor", None),
		"TransparentBackground": (108, 2, (3, 0), (), "TransparentBackground", None),
	}
		_prop_map_put_ = {
		"Brightness": ((100, LCID, 4, 0),()),
		"ColorType": ((101, LCID, 4, 0),()),
		"Contrast": ((102, LCID, 4, 0),()),
		"CropBottom": ((103, LCID, 4, 0),()),
		"CropLeft": ((104, LCID, 4, 0),()),
		"CropRight": ((105, LCID, 4, 0),()),
		"CropTop": ((106, LCID, 4, 0),()),
		"TransparencyColor": ((107, LCID, 4, 0),()),
		"TransparentBackground": ((108, LCID, 4, 0),()),
	}
class  PropertyTest (DispatchBaseClass) :
	CLSID = IID('{000C0333-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Condition": (2, 2, (3, 0), (), "Condition", None),
		"Connector": (5, 2, (3, 0), (), "Connector", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Name": (0, 2, (8, 0), (), "Name", None),
		"SecondValue": (4, 2, (12, 0), (), "SecondValue", None),
		"Value": (3, 2, (12, 0), (), "Value", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (8, 0), (), "Name", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  PropertyTests (DispatchBaseClass) :
	CLSID = IID('{000C0334-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Add(self, Name=defaultNamedNotOptArg, Condition=defaultNamedNotOptArg, Value=defaultNamedNotOptArg, SecondValue=defaultNamedNotOptArg
			, Connector=1):

		return self._oleobj_.InvokeTypes(5, LCID, 1, (24, 0), ((8, 1), (3, 1), (12, 17), (12, 17), (3, 49)),Name
			, Condition, Value, SecondValue, Connector)
 def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((3, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C0333-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Remove(self, Index=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(6, LCID, 1, (24, 0), ((3, 1),),Index
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (4, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((3, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C0333-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C0333-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(4, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  Script (DispatchBaseClass) :
	CLSID = IID('{000C0341-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Delete(self):

		return self._oleobj_.InvokeTypes(1610809352, LCID, 1, (24, 0), (),)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Extended": (1610809345, 2, (8, 0), (), "Extended", None),
		"Id": (1610809347, 2, (8, 0), (), "Id", None),
		"Language": (1610809349, 2, (3, 0), (), "Language", None),
		"Location": (1610809351, 2, (3, 0), (), "Location", None),
		"Parent": (1610809344, 2, (9, 0), (), "Parent", None),
		"ScriptText": (0, 2, (8, 0), (), "ScriptText", None),
		"Shape": (1610809353, 2, (9, 0), (), "Shape", None),
	}
		_prop_map_put_ = {
		"Extended": ((1610809345, LCID, 4, 0),()),
		"Id": ((1610809347, LCID, 4, 0),()),
		"Language": ((1610809349, LCID, 4, 0),()),
		"ScriptText": ((0, LCID, 4, 0),()),
	}
		
	def __call__(self):

		return self._ApplyTypes_(*(0, 2, (8, 0), (), "ScriptText", None))
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))

class  Scripts (DispatchBaseClass) :
	CLSID = IID('{000C0340-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Add(self, Anchor=None, Location=2, Language=2, Id=''
			, Extended='', ScriptText=''):

		return self._ApplyTypes_(1610809348, 1, (9, 32), ((9, 49), (3, 49), (3, 49), (8, 49), (8, 49), (8, 49)), 'Add', '{000C0341-0000-0000-C000-000000000046}',Anchor
			, Location, Language, Id, Extended, ScriptText
			)
 def Delete(self):

		return self._oleobj_.InvokeTypes(1610809349, LCID, 1, (24, 0), (),)
 def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C0341-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (1610809345, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Parent": (1610809344, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C0341-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C0341-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(1610809345, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  ShadowFormat (DispatchBaseClass) :
	CLSID = IID('{000C031B-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def IncrementOffsetX(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def IncrementOffsetY(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(11, LCID, 1, (24, 0), ((4, 1),),Increment
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		
		"ForeColor": (100, 2, (9, 0), (), "ForeColor", '{000C0312-0000-0000-C000-000000000046}'),
		"Obscured": (101, 2, (3, 0), (), "Obscured", None),
		"OffsetX": (102, 2, (4, 0), (), "OffsetX", None),
		"OffsetY": (103, 2, (4, 0), (), "OffsetY", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"Transparency": (104, 2, (4, 0), (), "Transparency", None),
		"Type": (105, 2, (3, 0), (), "Type", None),
		"Visible": (106, 2, (3, 0), (), "Visible", None),
	}
		_prop_map_put_ = {
		"ForeColor": ((100, LCID, 4, 0),()),
		"Obscured": ((101, LCID, 4, 0),()),
		"OffsetX": ((102, LCID, 4, 0),()),
		"OffsetY": ((103, LCID, 4, 0),()),
		"Transparency": ((104, LCID, 4, 0),()),
		"Type": ((105, LCID, 4, 0),()),
		"Visible": ((106, LCID, 4, 0),()),
	}
class  Shape (DispatchBaseClass) :
	CLSID = IID('{000C031C-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Apply(self):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), (),)
 def Delete(self):

		return self._oleobj_.InvokeTypes(11, LCID, 1, (24, 0), (),)
 def Duplicate(self):

		ret = self._oleobj_.InvokeTypes(12, LCID, 1, (9, 0), (),)

		if ret is not None:

			ret = Dispatch(ret, 'Duplicate', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Flip(self, FlipCmd=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(13, LCID, 1, (24, 0), ((3, 1),),FlipCmd
			)
 def IncrementLeft(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(14, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def IncrementRotation(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(15, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def IncrementTop(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(16, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def PickUp(self):

		return self._oleobj_.InvokeTypes(17, LCID, 1, (24, 0), (),)
 def RerouteConnections(self):

		return self._oleobj_.InvokeTypes(18, LCID, 1, (24, 0), (),)
 def ScaleHeight(self, Factor=defaultNamedNotOptArg, RelativeToOriginalSize=defaultNamedNotOptArg, fScale=0):

		return self._oleobj_.InvokeTypes(19, LCID, 1, (24, 0), ((4, 1), (3, 1), (3, 49)),Factor
			, RelativeToOriginalSize, fScale)
 def ScaleWidth(self, Factor=defaultNamedNotOptArg, RelativeToOriginalSize=defaultNamedNotOptArg, fScale=0):

		return self._oleobj_.InvokeTypes(20, LCID, 1, (24, 0), ((4, 1), (3, 1), (3, 49)),Factor
			, RelativeToOriginalSize, fScale)
 def Select(self, Replace=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(21, LCID, 1, (24, 0), ((12, 17),),Replace
			)
 def SetShapesDefaultProperties(self):

		return self._oleobj_.InvokeTypes(22, LCID, 1, (24, 0), (),)
 def Ungroup(self):

		ret = self._oleobj_.InvokeTypes(23, LCID, 1, (9, 0), (),)

		if ret is not None:

			ret = Dispatch(ret, 'Ungroup', '{000C031D-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def ZOrder(self, ZOrderCmd=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(24, LCID, 1, (24, 0), ((3, 1),),ZOrderCmd
			)

	_prop_map_get_ = {
		
		"Adjustments": (100, 2, (9, 0), (), "Adjustments", '{000C0310-0000-0000-C000-000000000046}'),
		"AlternativeText": (131, 2, (8, 0), (), "AlternativeText", None),
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"AutoShapeType": (101, 2, (3, 0), (), "AutoShapeType", None),
		"BlackWhiteMode": (102, 2, (3, 0), (), "BlackWhiteMode", None),
		
		"Callout": (103, 2, (9, 0), (), "Callout", '{000C0311-0000-0000-C000-000000000046}'),
		"ConnectionSiteCount": (104, 2, (3, 0), (), "ConnectionSiteCount", None),
		"Connector": (105, 2, (3, 0), (), "Connector", None),
		
		"ConnectorFormat": (106, 2, (9, 0), (), "ConnectorFormat", '{000C0313-0000-0000-C000-000000000046}'),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		
		"Fill": (107, 2, (9, 0), (), "Fill", '{000C0314-0000-0000-C000-000000000046}'),
		
		"GroupItems": (108, 2, (9, 0), (), "GroupItems", '{000C0316-0000-0000-C000-000000000046}'),
		"Height": (109, 2, (4, 0), (), "Height", None),
		"HorizontalFlip": (110, 2, (3, 0), (), "HorizontalFlip", None),
		"Left": (111, 2, (4, 0), (), "Left", None),
		
		"Line": (112, 2, (9, 0), (), "Line", '{000C0317-0000-0000-C000-000000000046}'),
		"LockAspectRatio": (113, 2, (3, 0), (), "LockAspectRatio", None),
		"Name": (115, 2, (8, 0), (), "Name", None),
		
		"Nodes": (116, 2, (9, 0), (), "Nodes", '{000C0319-0000-0000-C000-000000000046}'),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		
		"PictureFormat": (118, 2, (9, 0), (), "PictureFormat", '{000C031A-0000-0000-C000-000000000046}'),
		"Rotation": (117, 2, (4, 0), (), "Rotation", None),
		
		"Script": (130, 2, (9, 0), (), "Script", '{000C0341-0000-0000-C000-000000000046}'),
		
		"Shadow": (119, 2, (9, 0), (), "Shadow", '{000C031B-0000-0000-C000-000000000046}'),
		
		"TextEffect": (120, 2, (9, 0), (), "TextEffect", '{000C031F-0000-0000-C000-000000000046}'),
		
		"TextFrame": (121, 2, (9, 0), (), "TextFrame", '{000C0320-0000-0000-C000-000000000046}'),
		
		"ThreeD": (122, 2, (9, 0), (), "ThreeD", '{000C0321-0000-0000-C000-000000000046}'),
		"Top": (123, 2, (4, 0), (), "Top", None),
		"Type": (124, 2, (3, 0), (), "Type", None),
		"VerticalFlip": (125, 2, (3, 0), (), "VerticalFlip", None),
		"Vertices": (126, 2, (12, 0), (), "Vertices", None),
		"Visible": (127, 2, (3, 0), (), "Visible", None),
		"Width": (128, 2, (4, 0), (), "Width", None),
		"ZOrderPosition": (129, 2, (3, 0), (), "ZOrderPosition", None),
	}
		_prop_map_put_ = {
		"AlternativeText": ((131, LCID, 4, 0),()),
		"AutoShapeType": ((101, LCID, 4, 0),()),
		"BlackWhiteMode": ((102, LCID, 4, 0),()),
		"Height": ((109, LCID, 4, 0),()),
		"Left": ((111, LCID, 4, 0),()),
		"LockAspectRatio": ((113, LCID, 4, 0),()),
		"Name": ((115, LCID, 4, 0),()),
		"Rotation": ((117, LCID, 4, 0),()),
		"Top": ((123, LCID, 4, 0),()),
		"Visible": ((127, LCID, 4, 0),()),
		"Width": ((128, LCID, 4, 0),()),
	}
class  ShapeNode (DispatchBaseClass) :
	CLSID = IID('{000C0318-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"EditingType": (100, 2, (3, 0), (), "EditingType", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"Points": (101, 2, (12, 0), (), "Points", None),
		"SegmentType": (102, 2, (3, 0), (), "SegmentType", None),
	}
		_prop_map_put_ = {
	}
class  ShapeNodes (DispatchBaseClass) :
	CLSID = IID('{000C0319-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Delete(self, Index=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(11, LCID, 1, (24, 0), ((3, 1),),Index
			)
 def Insert(self, Index=defaultNamedNotOptArg, SegmentType=defaultNamedNotOptArg, EditingType=defaultNamedNotOptArg, X1=defaultNamedNotOptArg
			, Y1=defaultNamedNotOptArg, X2=0.0, Y2=0.0, X3=0.0, Y3=0.0):

		return self._oleobj_.InvokeTypes(12, LCID, 1, (24, 0), ((3, 1), (3, 1), (3, 1), (4, 1), (4, 1), (4, 49), (4, 49), (4, 49), (4, 49)),Index
			, SegmentType, EditingType, X1, Y1, X2
			, Y2, X3, Y3)
 def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C0318-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def SetEditingType(self, Index=defaultNamedNotOptArg, EditingType=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(13, LCID, 1, (24, 0), ((3, 1), (3, 1)),Index
			, EditingType)
 def SetPosition(self, Index=defaultNamedNotOptArg, X1=defaultNamedNotOptArg, Y1=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(14, LCID, 1, (24, 0), ((3, 1), (4, 1), (4, 1)),Index
			, X1, Y1)
 def SetSegmentType(self, Index=defaultNamedNotOptArg, SegmentType=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(15, LCID, 1, (24, 0), ((3, 1), (3, 1)),Index
			, SegmentType)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (2, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C0318-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C0318-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(2, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  ShapeRange (DispatchBaseClass) :
	CLSID = IID('{000C031D-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def Align(self, AlignCmd=defaultNamedNotOptArg, RelativeTo=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), ((3, 1), (3, 1)),AlignCmd
			, RelativeTo)
 def Apply(self):

		return self._oleobj_.InvokeTypes(11, LCID, 1, (24, 0), (),)
 def Delete(self):

		return self._oleobj_.InvokeTypes(12, LCID, 1, (24, 0), (),)
 def Distribute(self, DistributeCmd=defaultNamedNotOptArg, RelativeTo=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(13, LCID, 1, (24, 0), ((3, 1), (3, 1)),DistributeCmd
			, RelativeTo)
 def Duplicate(self):

		ret = self._oleobj_.InvokeTypes(14, LCID, 1, (9, 0), (),)

		if ret is not None:

			ret = Dispatch(ret, 'Duplicate', '{000C031D-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Flip(self, FlipCmd=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(15, LCID, 1, (24, 0), ((3, 1),),FlipCmd
			)
 def Group(self):

		ret = self._oleobj_.InvokeTypes(19, LCID, 1, (9, 0), (),)

		if ret is not None:

			ret = Dispatch(ret, 'Group', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def IncrementLeft(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(16, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def IncrementRotation(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(17, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def IncrementTop(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(18, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def PickUp(self):

		return self._oleobj_.InvokeTypes(20, LCID, 1, (24, 0), (),)
 def Regroup(self):

		ret = self._oleobj_.InvokeTypes(21, LCID, 1, (9, 0), (),)

		if ret is not None:

			ret = Dispatch(ret, 'Regroup', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def RerouteConnections(self):

		return self._oleobj_.InvokeTypes(22, LCID, 1, (24, 0), (),)
 def ScaleHeight(self, Factor=defaultNamedNotOptArg, RelativeToOriginalSize=defaultNamedNotOptArg, fScale=0):

		return self._oleobj_.InvokeTypes(23, LCID, 1, (24, 0), ((4, 1), (3, 1), (3, 49)),Factor
			, RelativeToOriginalSize, fScale)
 def ScaleWidth(self, Factor=defaultNamedNotOptArg, RelativeToOriginalSize=defaultNamedNotOptArg, fScale=0):

		return self._oleobj_.InvokeTypes(24, LCID, 1, (24, 0), ((4, 1), (3, 1), (3, 49)),Factor
			, RelativeToOriginalSize, fScale)
 def Select(self, Replace=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(25, LCID, 1, (24, 0), ((12, 17),),Replace
			)
 def SetShapesDefaultProperties(self):

		return self._oleobj_.InvokeTypes(26, LCID, 1, (24, 0), (),)
 def Ungroup(self):

		ret = self._oleobj_.InvokeTypes(27, LCID, 1, (9, 0), (),)

		if ret is not None:

			ret = Dispatch(ret, 'Ungroup', '{000C031D-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def ZOrder(self, ZOrderCmd=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(28, LCID, 1, (24, 0), ((3, 1),),ZOrderCmd
			)

	_prop_map_get_ = {
		
		"Adjustments": (100, 2, (9, 0), (), "Adjustments", '{000C0310-0000-0000-C000-000000000046}'),
		"AlternativeText": (131, 2, (8, 0), (), "AlternativeText", None),
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"AutoShapeType": (101, 2, (3, 0), (), "AutoShapeType", None),
		"BlackWhiteMode": (102, 2, (3, 0), (), "BlackWhiteMode", None),
		
		"Callout": (103, 2, (9, 0), (), "Callout", '{000C0311-0000-0000-C000-000000000046}'),
		"ConnectionSiteCount": (104, 2, (3, 0), (), "ConnectionSiteCount", None),
		"Connector": (105, 2, (3, 0), (), "Connector", None),
		
		"ConnectorFormat": (106, 2, (9, 0), (), "ConnectorFormat", '{000C0313-0000-0000-C000-000000000046}'),
		"Count": (2, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		
		"Fill": (107, 2, (9, 0), (), "Fill", '{000C0314-0000-0000-C000-000000000046}'),
		
		"GroupItems": (108, 2, (9, 0), (), "GroupItems", '{000C0316-0000-0000-C000-000000000046}'),
		"Height": (109, 2, (4, 0), (), "Height", None),
		"HorizontalFlip": (110, 2, (3, 0), (), "HorizontalFlip", None),
		"Left": (111, 2, (4, 0), (), "Left", None),
		
		"Line": (112, 2, (9, 0), (), "Line", '{000C0317-0000-0000-C000-000000000046}'),
		"LockAspectRatio": (113, 2, (3, 0), (), "LockAspectRatio", None),
		"Name": (115, 2, (8, 0), (), "Name", None),
		
		"Nodes": (116, 2, (9, 0), (), "Nodes", '{000C0319-0000-0000-C000-000000000046}'),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		
		"PictureFormat": (118, 2, (9, 0), (), "PictureFormat", '{000C031A-0000-0000-C000-000000000046}'),
		"Rotation": (117, 2, (4, 0), (), "Rotation", None),
		
		"Script": (130, 2, (9, 0), (), "Script", '{000C0341-0000-0000-C000-000000000046}'),
		
		"Shadow": (119, 2, (9, 0), (), "Shadow", '{000C031B-0000-0000-C000-000000000046}'),
		
		"TextEffect": (120, 2, (9, 0), (), "TextEffect", '{000C031F-0000-0000-C000-000000000046}'),
		
		"TextFrame": (121, 2, (9, 0), (), "TextFrame", '{000C0320-0000-0000-C000-000000000046}'),
		
		"ThreeD": (122, 2, (9, 0), (), "ThreeD", '{000C0321-0000-0000-C000-000000000046}'),
		"Top": (123, 2, (4, 0), (), "Top", None),
		"Type": (124, 2, (3, 0), (), "Type", None),
		"VerticalFlip": (125, 2, (3, 0), (), "VerticalFlip", None),
		"Vertices": (126, 2, (12, 0), (), "Vertices", None),
		"Visible": (127, 2, (3, 0), (), "Visible", None),
		"Width": (128, 2, (4, 0), (), "Width", None),
		"ZOrderPosition": (129, 2, (3, 0), (), "ZOrderPosition", None),
	}
		_prop_map_put_ = {
		"AlternativeText": ((131, LCID, 4, 0),()),
		"AutoShapeType": ((101, LCID, 4, 0),()),
		"BlackWhiteMode": ((102, LCID, 4, 0),()),
		"Height": ((109, LCID, 4, 0),()),
		"Left": ((111, LCID, 4, 0),()),
		"LockAspectRatio": ((113, LCID, 4, 0),()),
		"Name": ((115, LCID, 4, 0),()),
		"Rotation": ((117, LCID, 4, 0),()),
		"Top": ((123, LCID, 4, 0),()),
		"Visible": ((127, LCID, 4, 0),()),
		"Width": ((128, LCID, 4, 0),()),
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C031C-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(2, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  Shapes (DispatchBaseClass) :
	CLSID = IID('{000C031E-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def AddCallout(self, Type=defaultNamedNotOptArg, Left=defaultNamedNotOptArg, Top=defaultNamedNotOptArg, Width=defaultNamedNotOptArg
			, Height=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(10, LCID, 1, (9, 0), ((3, 1), (4, 1), (4, 1), (4, 1), (4, 1)),Type
			, Left, Top, Width, Height)

		if ret is not None:

			ret = Dispatch(ret, 'AddCallout', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddConnector(self, Type=defaultNamedNotOptArg, BeginX=defaultNamedNotOptArg, BeginY=defaultNamedNotOptArg, EndX=defaultNamedNotOptArg
			, EndY=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(11, LCID, 1, (9, 0), ((3, 1), (4, 1), (4, 1), (4, 1), (4, 1)),Type
			, BeginX, BeginY, EndX, EndY)

		if ret is not None:

			ret = Dispatch(ret, 'AddConnector', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddCurve(self, SafeArrayOfPoints=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(12, LCID, 1, (9, 0), ((12, 1),),SafeArrayOfPoints
			)

		if ret is not None:

			ret = Dispatch(ret, 'AddCurve', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddLabel(self, Orientation=defaultNamedNotOptArg, Left=defaultNamedNotOptArg, Top=defaultNamedNotOptArg, Width=defaultNamedNotOptArg
			, Height=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(13, LCID, 1, (9, 0), ((3, 1), (4, 1), (4, 1), (4, 1), (4, 1)),Orientation
			, Left, Top, Width, Height)

		if ret is not None:

			ret = Dispatch(ret, 'AddLabel', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddLine(self, BeginX=defaultNamedNotOptArg, BeginY=defaultNamedNotOptArg, EndX=defaultNamedNotOptArg, EndY=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(14, LCID, 1, (9, 0), ((4, 1), (4, 1), (4, 1), (4, 1)),BeginX
			, BeginY, EndX, EndY)

		if ret is not None:

			ret = Dispatch(ret, 'AddLine', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddPicture(self, FileName=defaultNamedNotOptArg, LinkToFile=defaultNamedNotOptArg, SaveWithDocument=defaultNamedNotOptArg, Left=defaultNamedNotOptArg
			, Top=defaultNamedNotOptArg, Width=-1.0, Height=-1.0):

		ret = self._oleobj_.InvokeTypes(15, LCID, 1, (9, 0), ((8, 1), (3, 1), (3, 1), (4, 1), (4, 1), (4, 49), (4, 49)),FileName
			, LinkToFile, SaveWithDocument, Left, Top, Width
			, Height)

		if ret is not None:

			ret = Dispatch(ret, 'AddPicture', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddPolyline(self, SafeArrayOfPoints=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(16, LCID, 1, (9, 0), ((12, 1),),SafeArrayOfPoints
			)

		if ret is not None:

			ret = Dispatch(ret, 'AddPolyline', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddShape(self, Type=defaultNamedNotOptArg, Left=defaultNamedNotOptArg, Top=defaultNamedNotOptArg, Width=defaultNamedNotOptArg
			, Height=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(17, LCID, 1, (9, 0), ((3, 1), (4, 1), (4, 1), (4, 1), (4, 1)),Type
			, Left, Top, Width, Height)

		if ret is not None:

			ret = Dispatch(ret, 'AddShape', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddTextEffect(self, PresetTextEffect=defaultNamedNotOptArg, Text=defaultNamedNotOptArg, FontName=defaultNamedNotOptArg, FontSize=defaultNamedNotOptArg
			, FontBold=defaultNamedNotOptArg, FontItalic=defaultNamedNotOptArg, Left=defaultNamedNotOptArg, Top=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(18, LCID, 1, (9, 0), ((3, 1), (8, 1), (8, 1), (4, 1), (3, 1), (3, 1), (4, 1), (4, 1)),PresetTextEffect
			, Text, FontName, FontSize, FontBold, FontItalic
			, Left, Top)

		if ret is not None:

			ret = Dispatch(ret, 'AddTextEffect', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddTextbox(self, Orientation=defaultNamedNotOptArg, Left=defaultNamedNotOptArg, Top=defaultNamedNotOptArg, Width=defaultNamedNotOptArg
			, Height=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(19, LCID, 1, (9, 0), ((3, 1), (4, 1), (4, 1), (4, 1), (4, 1)),Orientation
			, Left, Top, Width, Height)

		if ret is not None:

			ret = Dispatch(ret, 'AddTextbox', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def BuildFreeform(self, EditingType=defaultNamedNotOptArg, X1=defaultNamedNotOptArg, Y1=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(20, LCID, 1, (9, 0), ((3, 1), (4, 1), (4, 1)),EditingType
			, X1, Y1)

		if ret is not None:

			ret = Dispatch(ret, 'BuildFreeform', '{000C0315-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Range(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(21, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Range', '{000C031D-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def SelectAll(self):

		return self._oleobj_.InvokeTypes(22, LCID, 1, (24, 0), (),)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		
		"Background": (100, 2, (9, 0), (), "Background", '{000C031C-0000-0000-C000-000000000046}'),
		"Count": (2, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		
		"Default": (101, 2, (9, 0), (), "Default", '{000C031C-0000-0000-C000-000000000046}'),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 1, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C031C-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C031C-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(2, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  TextEffectFormat (DispatchBaseClass) :
	CLSID = IID('{000C031F-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def ToggleVerticalText(self):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), (),)

	_prop_map_get_ = {
		"Alignment": (100, 2, (3, 0), (), "Alignment", None),
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"FontBold": (101, 2, (3, 0), (), "FontBold", None),
		"FontItalic": (102, 2, (3, 0), (), "FontItalic", None),
		"FontName": (103, 2, (8, 0), (), "FontName", None),
		"FontSize": (104, 2, (4, 0), (), "FontSize", None),
		"KernedPairs": (105, 2, (3, 0), (), "KernedPairs", None),
		"NormalizedHeight": (106, 2, (3, 0), (), "NormalizedHeight", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"PresetShape": (107, 2, (3, 0), (), "PresetShape", None),
		"PresetTextEffect": (108, 2, (3, 0), (), "PresetTextEffect", None),
		"RotatedChars": (109, 2, (3, 0), (), "RotatedChars", None),
		"Text": (110, 2, (8, 0), (), "Text", None),
		"Tracking": (111, 2, (4, 0), (), "Tracking", None),
	}
		_prop_map_put_ = {
		"Alignment": ((100, LCID, 4, 0),()),
		"FontBold": ((101, LCID, 4, 0),()),
		"FontItalic": ((102, LCID, 4, 0),()),
		"FontName": ((103, LCID, 4, 0),()),
		"FontSize": ((104, LCID, 4, 0),()),
		"KernedPairs": ((105, LCID, 4, 0),()),
		"NormalizedHeight": ((106, LCID, 4, 0),()),
		"PresetShape": ((107, LCID, 4, 0),()),
		"PresetTextEffect": ((108, LCID, 4, 0),()),
		"RotatedChars": ((109, LCID, 4, 0),()),
		"Text": ((110, LCID, 4, 0),()),
		"Tracking": ((111, LCID, 4, 0),()),
	}
class  TextFrame (DispatchBaseClass) :
	CLSID = IID('{000C0320-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"MarginBottom": (100, 2, (4, 0), (), "MarginBottom", None),
		"MarginLeft": (101, 2, (4, 0), (), "MarginLeft", None),
		"MarginRight": (102, 2, (4, 0), (), "MarginRight", None),
		"MarginTop": (103, 2, (4, 0), (), "MarginTop", None),
		"Orientation": (104, 2, (3, 0), (), "Orientation", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
		"MarginBottom": ((100, LCID, 4, 0),()),
		"MarginLeft": ((101, LCID, 4, 0),()),
		"MarginRight": ((102, LCID, 4, 0),()),
		"MarginTop": ((103, LCID, 4, 0),()),
		"Orientation": ((104, LCID, 4, 0),()),
	}
class  ThreeDFormat (DispatchBaseClass) :
	CLSID = IID('{000C0321-0000-0000-C000-000000000046}')
		coclass_clsid = None
		def IncrementRotationX(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(10, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def IncrementRotationY(self, Increment=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(11, LCID, 1, (24, 0), ((4, 1),),Increment
			)
 def ResetRotation(self):

		return self._oleobj_.InvokeTypes(12, LCID, 1, (24, 0), (),)
 def SetExtrusionDirection(self, PresetExtrusionDirection=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(14, LCID, 1, (24, 0), ((3, 1),),PresetExtrusionDirection
			)
 def SetThreeDFormat(self, PresetThreeDFormat=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(13, LCID, 1, (24, 0), ((3, 1),),PresetThreeDFormat
			)

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"Depth": (100, 2, (4, 0), (), "Depth", None),
		
		"ExtrusionColor": (101, 2, (9, 0), (), "ExtrusionColor", '{000C0312-0000-0000-C000-000000000046}'),
		"ExtrusionColorType": (102, 2, (3, 0), (), "ExtrusionColorType", None),
		"Parent": (1, 2, (9, 0), (), "Parent", None),
		"Perspective": (103, 2, (3, 0), (), "Perspective", None),
		"PresetExtrusionDirection": (104, 2, (3, 0), (), "PresetExtrusionDirection", None),
		"PresetLightingDirection": (105, 2, (3, 0), (), "PresetLightingDirection", None),
		"PresetLightingSoftness": (106, 2, (3, 0), (), "PresetLightingSoftness", None),
		"PresetMaterial": (107, 2, (3, 0), (), "PresetMaterial", None),
		"PresetThreeDFormat": (108, 2, (3, 0), (), "PresetThreeDFormat", None),
		"RotationX": (109, 2, (4, 0), (), "RotationX", None),
		"RotationY": (110, 2, (4, 0), (), "RotationY", None),
		"Visible": (111, 2, (3, 0), (), "Visible", None),
	}
		_prop_map_put_ = {
		"Depth": ((100, LCID, 4, 0),()),
		"ExtrusionColorType": ((102, LCID, 4, 0),()),
		"Perspective": ((103, LCID, 4, 0),()),
		"PresetLightingDirection": ((105, LCID, 4, 0),()),
		"PresetLightingSoftness": ((106, LCID, 4, 0),()),
		"PresetMaterial": ((107, LCID, 4, 0),()),
		"RotationX": ((109, LCID, 4, 0),()),
		"RotationY": ((110, LCID, 4, 0),()),
		"Visible": ((111, LCID, 4, 0),()),
	}
class  WebPageFont (DispatchBaseClass) :
	CLSID = IID('{000C0913-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"FixedWidthFont": (12, 2, (8, 0), (), "FixedWidthFont", None),
		"FixedWidthFontSize": (13, 2, (4, 0), (), "FixedWidthFontSize", None),
		"ProportionalFont": (10, 2, (8, 0), (), "ProportionalFont", None),
		"ProportionalFontSize": (11, 2, (4, 0), (), "ProportionalFontSize", None),
	}
		_prop_map_put_ = {
		"FixedWidthFont": ((12, LCID, 4, 0),()),
		"FixedWidthFontSize": ((13, LCID, 4, 0),()),
		"ProportionalFont": ((10, LCID, 4, 0),()),
		"ProportionalFontSize": ((11, LCID, 4, 0),()),
	}
class  WebPageFonts (DispatchBaseClass) :
	CLSID = IID('{000C0914-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	
	def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((3, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C0913-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret

	_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (1, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
	}
		_prop_map_put_ = {
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((3, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C0913-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C0913-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(1, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  _CommandBarActiveX (DispatchBaseClass) :
	CLSID = IID('{000C030D-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def Copy(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874886, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Copy', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Delete(self, Temporary=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610874887, LCID, 1, (24, 0), ((12, 17),),Temporary
			)
 def EnsureControl(self):

		return self._oleobj_.InvokeTypes(1610940420, LCID, 1, (24, 0), (),)
 def Execute(self):

		return self._oleobj_.InvokeTypes(1610874892, LCID, 1, (24, 0), (),)
 def GetaccDefaultAction(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5013, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccDescription(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5005, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelp(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5008, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelpTopic(self, pszHelpFile=pythoncom.Missing, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5009, 2, (3, 0), ((16392, 2), (12, 17)), 'GetaccHelpTopic', None,pszHelpFile
			, varChild)
 def GetaccKeyboardShortcut(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5010, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccName(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5003, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccRole(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5006, 2, (12, 0), ((12, 17),), 'GetaccRole', None,varChild
			)
 def GetaccState(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5007, 2, (12, 0), ((12, 17),), 'GetaccState', None,varChild
			)
 def GetaccValue(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5004, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def Move(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874902, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Move', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def QueryControlInterface(self, bstrIid=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(1610940418, LCID, 2, (13, 0), ((8, 1),),bstrIid
			)

		if ret is not None:

			
			try:

				ret = ret.QueryInterface(pythoncom.IID_IDispatch)

			except pythoncom.error:

				return ret

			ret = Dispatch(ret, 'QueryControlInterface', None, UnicodeToString=0)

		return ret
 def Reserved1(self):

		return self._oleobj_.InvokeTypes(1610874926, LCID, 1, (24, 0), (),)
 def Reserved2(self):

		return self._oleobj_.InvokeTypes(1610874927, LCID, 1, (24, 0), (),)
 def Reserved3(self):

		return self._oleobj_.InvokeTypes(1610874928, LCID, 1, (24, 0), (),)
 def Reserved4(self):

		return self._oleobj_.InvokeTypes(1610874929, LCID, 1, (24, 0), (),)
 def Reserved5(self):

		return self._oleobj_.InvokeTypes(1610874930, LCID, 1, (24, 0), (),)
 def Reserved6(self):

		return self._oleobj_.InvokeTypes(1610874931, LCID, 1, (24, 0), (),)
 def Reserved7(self):

		return self._oleobj_.InvokeTypes(1610874932, LCID, 1, (24, 0), (),)
 def Reset(self):

		return self._oleobj_.InvokeTypes(1610874913, LCID, 1, (24, 0), (),)
 def SetFocus(self):

		return self._oleobj_.InvokeTypes(1610874914, LCID, 1, (24, 0), (),)
 def SetInnerObjectFactory(self, pUnk=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1610940419, LCID, 1, (24, 0), ((13, 1),),pUnk
			)
 def SetaccName(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5003, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def SetaccValue(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5004, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def accChild(self, varChild=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(-5002, LCID, 2, (9, 0), ((12, 1),),varChild
			)

		if ret is not None:

			ret = Dispatch(ret, 'accChild', None, UnicodeToString=0)

		return ret
 def accDoDefaultAction(self, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5018, LCID, 1, (24, 0), ((12, 17),),varChild
			)
 def accHitTest(self, xLeft=defaultNamedNotOptArg, yTop=defaultNamedNotOptArg):

		return self._ApplyTypes_(-5017, 1, (12, 0), ((3, 1), (3, 1)), 'accHitTest', None,xLeft
			, yTop)
 def accLocation(self, pxLeft=pythoncom.Missing, pyTop=pythoncom.Missing, pcxWidth=pythoncom.Missing, pcyHeight=pythoncom.Missing
			, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5015, 1, (24, 0), ((16387, 2), (16387, 2), (16387, 2), (16387, 2), (12, 17)), 'accLocation', None,pxLeft
			, pyTop, pcxWidth, pcyHeight, varChild)
 def accNavigate(self, navDir=defaultNamedNotOptArg, varStart=defaultNamedOptArg):

		return self._ApplyTypes_(-5016, 1, (12, 0), ((3, 1), (12, 17)), 'accNavigate', None,navDir
			, varStart)
 def accSelect(self, flagsSelect=defaultNamedNotOptArg, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5014, LCID, 1, (24, 0), ((3, 1), (12, 17)),flagsSelect
			, varChild)

	_prop_map_get_ = {
		"Application": (1610809344, 2, (9, 0), (), "Application", None),
		"BeginGroup": (1610874880, 2, (11, 0), (), "BeginGroup", None),
		"BuiltIn": (1610874882, 2, (11, 0), (), "BuiltIn", None),
		"Caption": (1610874883, 2, (8, 0), (), "Caption", None),
		"Control": (1610874885, 2, (9, 0), (), "Control", None),
		"ControlCLSID": (1610940416, 2, (8, 0), (), "ControlCLSID", None),
		"Creator": (1610809345, 2, (3, 0), (), "Creator", None),
		"DescriptionText": (1610874888, 2, (8, 0), (), "DescriptionText", None),
		"Enabled": (1610874890, 2, (11, 0), (), "Enabled", None),
		"Height": (1610874893, 2, (3, 0), (), "Height", None),
		"HelpContextId": (1610874895, 2, (3, 0), (), "HelpContextId", None),
		"HelpFile": (1610874897, 2, (8, 0), (), "HelpFile", None),
		"Id": (1610874899, 2, (3, 0), (), "Id", None),
		"Index": (1610874900, 2, (3, 0), (), "Index", None),
		"InstanceId": (1610874901, 2, (3, 0), (), "InstanceId", None),
		"IsPriorityDropped": (1610874925, 2, (11, 0), (), "IsPriorityDropped", None),
		"Left": (1610874903, 2, (3, 0), (), "Left", None),
		"OLEUsage": (1610874904, 2, (3, 0), (), "OLEUsage", None),
		"OnAction": (1610874906, 2, (8, 0), (), "OnAction", None),
		"Parameter": (1610874909, 2, (8, 0), (), "Parameter", None),
		
		"Parent": (1610874908, 2, (9, 0), (), "Parent", '{000C0304-0000-0000-C000-000000000046}'),
		"Priority": (1610874911, 2, (3, 0), (), "Priority", None),
		"Tag": (1610874915, 2, (8, 0), (), "Tag", None),
		"TooltipText": (1610874917, 2, (8, 0), (), "TooltipText", None),
		"Top": (1610874919, 2, (3, 0), (), "Top", None),
		"Type": (1610874920, 2, (3, 0), (), "Type", None),
		"Visible": (1610874921, 2, (11, 0), (), "Visible", None),
		"Width": (1610874923, 2, (3, 0), (), "Width", None),
		"accChildCount": (-5001, 2, (3, 0), (), "accChildCount", None),
		"accDefaultAction": (-5013, 2, (8, 0), ((12, 17),), "accDefaultAction", None),
		"accDescription": (-5005, 2, (8, 0), ((12, 17),), "accDescription", None),
		"accFocus": (-5011, 2, (12, 0), (), "accFocus", None),
		"accHelp": (-5008, 2, (8, 0), ((12, 17),), "accHelp", None),
		"accHelpTopic": (-5009, 2, (3, 0), ((16392, 2), (12, 17)), "accHelpTopic", None),
		"accKeyboardShortcut": (-5010, 2, (8, 0), ((12, 17),), "accKeyboardShortcut", None),
		"accName": (-5003, 2, (8, 0), ((12, 17),), "accName", None),
		"accParent": (-5000, 2, (9, 0), (), "accParent", None),
		"accRole": (-5006, 2, (12, 0), ((12, 17),), "accRole", None),
		"accSelection": (-5012, 2, (12, 0), (), "accSelection", None),
		"accState": (-5007, 2, (12, 0), ((12, 17),), "accState", None),
		"accValue": (-5004, 2, (8, 0), ((12, 17),), "accValue", None),
	}
		_prop_map_put_ = {
		"BeginGroup": ((1610874880, LCID, 4, 0),()),
		"Caption": ((1610874883, LCID, 4, 0),()),
		"ControlCLSID": ((1610940416, LCID, 4, 0),()),
		"DescriptionText": ((1610874888, LCID, 4, 0),()),
		"Enabled": ((1610874890, LCID, 4, 0),()),
		"Height": ((1610874893, LCID, 4, 0),()),
		"HelpContextId": ((1610874895, LCID, 4, 0),()),
		"HelpFile": ((1610874897, LCID, 4, 0),()),
		"InitWith": ((1610940421, LCID, 4, 0),()),
		"OLEUsage": ((1610874904, LCID, 4, 0),()),
		"OnAction": ((1610874906, LCID, 4, 0),()),
		"Parameter": ((1610874909, LCID, 4, 0),()),
		"Priority": ((1610874911, LCID, 4, 0),()),
		"Tag": ((1610874915, LCID, 4, 0),()),
		"TooltipText": ((1610874917, LCID, 4, 0),()),
		"Visible": ((1610874921, LCID, 4, 0),()),
		"Width": ((1610874923, LCID, 4, 0),()),
		"accName": ((-5003, LCID, 4, 0),()),
		"accValue": ((-5004, LCID, 4, 0),()),
	}
class  _CommandBarButton (DispatchBaseClass) :
	CLSID = IID('{000C030E-0000-0000-C000-000000000046}')
		coclass_clsid = IID('{55F88891-7708-11D1-ACEB-006008961DA5}')
		
	def Copy(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874886, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Copy', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def CopyFace(self):

		return self._oleobj_.InvokeTypes(1610940418, LCID, 1, (24, 0), (),)
 def Delete(self, Temporary=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610874887, LCID, 1, (24, 0), ((12, 17),),Temporary
			)
 def Execute(self):

		return self._oleobj_.InvokeTypes(1610874892, LCID, 1, (24, 0), (),)
 def GetaccDefaultAction(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5013, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccDescription(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5005, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelp(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5008, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelpTopic(self, pszHelpFile=pythoncom.Missing, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5009, 2, (3, 0), ((16392, 2), (12, 17)), 'GetaccHelpTopic', None,pszHelpFile
			, varChild)
 def GetaccKeyboardShortcut(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5010, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccName(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5003, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccRole(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5006, 2, (12, 0), ((12, 17),), 'GetaccRole', None,varChild
			)
 def GetaccState(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5007, 2, (12, 0), ((12, 17),), 'GetaccState', None,varChild
			)
 def GetaccValue(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5004, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def Move(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874902, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Move', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def PasteFace(self):

		return self._oleobj_.InvokeTypes(1610940421, LCID, 1, (24, 0), (),)
 def Reserved1(self):

		return self._oleobj_.InvokeTypes(1610874926, LCID, 1, (24, 0), (),)
 def Reserved2(self):

		return self._oleobj_.InvokeTypes(1610874927, LCID, 1, (24, 0), (),)
 def Reserved3(self):

		return self._oleobj_.InvokeTypes(1610874928, LCID, 1, (24, 0), (),)
 def Reserved4(self):

		return self._oleobj_.InvokeTypes(1610874929, LCID, 1, (24, 0), (),)
 def Reserved5(self):

		return self._oleobj_.InvokeTypes(1610874930, LCID, 1, (24, 0), (),)
 def Reserved6(self):

		return self._oleobj_.InvokeTypes(1610874931, LCID, 1, (24, 0), (),)
 def Reserved7(self):

		return self._oleobj_.InvokeTypes(1610874932, LCID, 1, (24, 0), (),)
 def Reset(self):

		return self._oleobj_.InvokeTypes(1610874913, LCID, 1, (24, 0), (),)
 def SetFocus(self):

		return self._oleobj_.InvokeTypes(1610874914, LCID, 1, (24, 0), (),)
 def SetaccName(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5003, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def SetaccValue(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5004, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def accChild(self, varChild=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(-5002, LCID, 2, (9, 0), ((12, 1),),varChild
			)

		if ret is not None:

			ret = Dispatch(ret, 'accChild', None, UnicodeToString=0)

		return ret
 def accDoDefaultAction(self, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5018, LCID, 1, (24, 0), ((12, 17),),varChild
			)
 def accHitTest(self, xLeft=defaultNamedNotOptArg, yTop=defaultNamedNotOptArg):

		return self._ApplyTypes_(-5017, 1, (12, 0), ((3, 1), (3, 1)), 'accHitTest', None,xLeft
			, yTop)
 def accLocation(self, pxLeft=pythoncom.Missing, pyTop=pythoncom.Missing, pcxWidth=pythoncom.Missing, pcyHeight=pythoncom.Missing
			, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5015, 1, (24, 0), ((16387, 2), (16387, 2), (16387, 2), (16387, 2), (12, 17)), 'accLocation', None,pxLeft
			, pyTop, pcxWidth, pcyHeight, varChild)
 def accNavigate(self, navDir=defaultNamedNotOptArg, varStart=defaultNamedOptArg):

		return self._ApplyTypes_(-5016, 1, (12, 0), ((3, 1), (12, 17)), 'accNavigate', None,navDir
			, varStart)
 def accSelect(self, flagsSelect=defaultNamedNotOptArg, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5014, LCID, 1, (24, 0), ((3, 1), (12, 17)),flagsSelect
			, varChild)

	_prop_map_get_ = {
		"Application": (1610809344, 2, (9, 0), (), "Application", None),
		"BeginGroup": (1610874880, 2, (11, 0), (), "BeginGroup", None),
		"BuiltIn": (1610874882, 2, (11, 0), (), "BuiltIn", None),
		"BuiltInFace": (1610940416, 2, (11, 0), (), "BuiltInFace", None),
		"Caption": (1610874883, 2, (8, 0), (), "Caption", None),
		"Control": (1610874885, 2, (9, 0), (), "Control", None),
		"Creator": (1610809345, 2, (3, 0), (), "Creator", None),
		"DescriptionText": (1610874888, 2, (8, 0), (), "DescriptionText", None),
		"Enabled": (1610874890, 2, (11, 0), (), "Enabled", None),
		"FaceId": (1610940419, 2, (3, 0), (), "FaceId", None),
		"Height": (1610874893, 2, (3, 0), (), "Height", None),
		"HelpContextId": (1610874895, 2, (3, 0), (), "HelpContextId", None),
		"HelpFile": (1610874897, 2, (8, 0), (), "HelpFile", None),
		"HyperlinkType": (1610940428, 2, (3, 0), (), "HyperlinkType", None),
		"Id": (1610874899, 2, (3, 0), (), "Id", None),
		"Index": (1610874900, 2, (3, 0), (), "Index", None),
		"InstanceId": (1610874901, 2, (3, 0), (), "InstanceId", None),
		"IsPriorityDropped": (1610874925, 2, (11, 0), (), "IsPriorityDropped", None),
		"Left": (1610874903, 2, (3, 0), (), "Left", None),
		"OLEUsage": (1610874904, 2, (3, 0), (), "OLEUsage", None),
		"OnAction": (1610874906, 2, (8, 0), (), "OnAction", None),
		"Parameter": (1610874909, 2, (8, 0), (), "Parameter", None),
		
		"Parent": (1610874908, 2, (9, 0), (), "Parent", '{000C0304-0000-0000-C000-000000000046}'),
		"Priority": (1610874911, 2, (3, 0), (), "Priority", None),
		"ShortcutText": (1610940422, 2, (8, 0), (), "ShortcutText", None),
		"State": (1610940424, 2, (3, 0), (), "State", None),
		"Style": (1610940426, 2, (3, 0), (), "Style", None),
		"Tag": (1610874915, 2, (8, 0), (), "Tag", None),
		"TooltipText": (1610874917, 2, (8, 0), (), "TooltipText", None),
		"Top": (1610874919, 2, (3, 0), (), "Top", None),
		"Type": (1610874920, 2, (3, 0), (), "Type", None),
		"Visible": (1610874921, 2, (11, 0), (), "Visible", None),
		"Width": (1610874923, 2, (3, 0), (), "Width", None),
		"accChildCount": (-5001, 2, (3, 0), (), "accChildCount", None),
		"accDefaultAction": (-5013, 2, (8, 0), ((12, 17),), "accDefaultAction", None),
		"accDescription": (-5005, 2, (8, 0), ((12, 17),), "accDescription", None),
		"accFocus": (-5011, 2, (12, 0), (), "accFocus", None),
		"accHelp": (-5008, 2, (8, 0), ((12, 17),), "accHelp", None),
		"accHelpTopic": (-5009, 2, (3, 0), ((16392, 2), (12, 17)), "accHelpTopic", None),
		"accKeyboardShortcut": (-5010, 2, (8, 0), ((12, 17),), "accKeyboardShortcut", None),
		"accName": (-5003, 2, (8, 0), ((12, 17),), "accName", None),
		"accParent": (-5000, 2, (9, 0), (), "accParent", None),
		"accRole": (-5006, 2, (12, 0), ((12, 17),), "accRole", None),
		"accSelection": (-5012, 2, (12, 0), (), "accSelection", None),
		"accState": (-5007, 2, (12, 0), ((12, 17),), "accState", None),
		"accValue": (-5004, 2, (8, 0), ((12, 17),), "accValue", None),
	}
		_prop_map_put_ = {
		"BeginGroup": ((1610874880, LCID, 4, 0),()),
		"BuiltInFace": ((1610940416, LCID, 4, 0),()),
		"Caption": ((1610874883, LCID, 4, 0),()),
		"DescriptionText": ((1610874888, LCID, 4, 0),()),
		"Enabled": ((1610874890, LCID, 4, 0),()),
		"FaceId": ((1610940419, LCID, 4, 0),()),
		"Height": ((1610874893, LCID, 4, 0),()),
		"HelpContextId": ((1610874895, LCID, 4, 0),()),
		"HelpFile": ((1610874897, LCID, 4, 0),()),
		"HyperlinkType": ((1610940428, LCID, 4, 0),()),
		"OLEUsage": ((1610874904, LCID, 4, 0),()),
		"OnAction": ((1610874906, LCID, 4, 0),()),
		"Parameter": ((1610874909, LCID, 4, 0),()),
		"Priority": ((1610874911, LCID, 4, 0),()),
		"ShortcutText": ((1610940422, LCID, 4, 0),()),
		"State": ((1610940424, LCID, 4, 0),()),
		"Style": ((1610940426, LCID, 4, 0),()),
		"Tag": ((1610874915, LCID, 4, 0),()),
		"TooltipText": ((1610874917, LCID, 4, 0),()),
		"Visible": ((1610874921, LCID, 4, 0),()),
		"Width": ((1610874923, LCID, 4, 0),()),
		"accName": ((-5003, LCID, 4, 0),()),
		"accValue": ((-5004, LCID, 4, 0),()),
	}
class  _CommandBarButtonEvents :
	CLSID = CLSID_Sink = IID('{000C0351-0000-0000-C000-000000000046}')
		coclass_clsid = IID('{55F88891-7708-11D1-ACEB-006008961DA5}')
		_public_methods_ = []
		_dispid_to_func_ = {
		1610678275 : "OnInvoke",
		1610678273 : "OnGetTypeInfo",
		        1 : "OnClick",
		1610612737 : "OnAddRef",
		1610612736 : "OnQueryInterface",
		1610612738 : "OnRelease",
		1610678274 : "OnGetIDsOfNames",
		1610678272 : "OnGetTypeInfoCount",
		}
		def __init__(self, oobj = None):

		if oobj is None:

			self._olecp = None

		else:

			import win32com.server.util

			from win32com.server.policy import EventHandlerPolicy

			cpc=oobj._oleobj_.QueryInterface(pythoncom.IID_IConnectionPointContainer)

			cp=cpc.FindConnectionPoint(self.CLSID_Sink)

			cookie=cp.Advise(win32com.server.util.wrap(self, usePolicy=EventHandlerPolicy))

			self._olecp,self._olecp_cookie = cp,cookie
 def __del__(self):

		try:

			self.close()

		except pythoncom.com_error:

			pass
 def close(self):

		if self._olecp is not None:

			cp,cookie,self._olecp,self._olecp_cookie = self._olecp,self._olecp_cookie,None,None

			cp.Unadvise(cookie)
 def _query_interface_(self, iid):

		import win32com.server.util

		if iid==self.CLSID_Sink: return win32com.server.util.wrap(self)

class  _CommandBarComboBox (DispatchBaseClass) :
	CLSID = IID('{000C030C-0000-0000-C000-000000000046}')
		coclass_clsid = IID('{55F88897-7708-11D1-ACEB-006008961DA5}')
		def AddItem(self, Text=defaultNamedNotOptArg, Index=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610940416, LCID, 1, (24, 0), ((8, 1), (12, 17)),Text
			, Index)
 def Clear(self):

		return self._oleobj_.InvokeTypes(1610940417, LCID, 1, (24, 0), (),)
 def Copy(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874886, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Copy', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def Delete(self, Temporary=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(1610874887, LCID, 1, (24, 0), ((12, 17),),Temporary
			)
 def Execute(self):

		return self._oleobj_.InvokeTypes(1610874892, LCID, 1, (24, 0), (),)
 def GetaccDefaultAction(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5013, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccDescription(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5005, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelp(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5008, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelpTopic(self, pszHelpFile=pythoncom.Missing, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5009, 2, (3, 0), ((16392, 2), (12, 17)), 'GetaccHelpTopic', None,pszHelpFile
			, varChild)
 def GetaccKeyboardShortcut(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5010, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccName(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5003, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccRole(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5006, 2, (12, 0), ((12, 17),), 'GetaccRole', None,varChild
			)
 def GetaccState(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5007, 2, (12, 0), ((12, 17),), 'GetaccState', None,varChild
			)
 def GetaccValue(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5004, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def List(self, Index=defaultNamedNotOptArg):

		
		return self._oleobj_.InvokeTypes(1610940422, LCID, 2, (8, 0), ((3, 1),),Index
			)
 def Move(self, Bar=defaultNamedOptArg, Before=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610874902, LCID, 1, (9, 0), ((12, 17), (12, 17)),Bar
			, Before)

		if ret is not None:

			ret = Dispatch(ret, 'Move', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def RemoveItem(self, Index=defaultNamedNotOptArg):

		return self._oleobj_.InvokeTypes(1610940429, LCID, 1, (24, 0), ((3, 1),),Index
			)
 def Reserved1(self):

		return self._oleobj_.InvokeTypes(1610874926, LCID, 1, (24, 0), (),)
 def Reserved2(self):

		return self._oleobj_.InvokeTypes(1610874927, LCID, 1, (24, 0), (),)
 def Reserved3(self):

		return self._oleobj_.InvokeTypes(1610874928, LCID, 1, (24, 0), (),)
 def Reserved4(self):

		return self._oleobj_.InvokeTypes(1610874929, LCID, 1, (24, 0), (),)
 def Reserved5(self):

		return self._oleobj_.InvokeTypes(1610874930, LCID, 1, (24, 0), (),)
 def Reserved6(self):

		return self._oleobj_.InvokeTypes(1610874931, LCID, 1, (24, 0), (),)
 def Reserved7(self):

		return self._oleobj_.InvokeTypes(1610874932, LCID, 1, (24, 0), (),)
 def Reset(self):

		return self._oleobj_.InvokeTypes(1610874913, LCID, 1, (24, 0), (),)
 def SetFocus(self):

		return self._oleobj_.InvokeTypes(1610874914, LCID, 1, (24, 0), (),)
 def SetList(self, Index=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(1610940422, LCID, 4, (24, 0), ((3, 1), (8, 1)),Index
			, arg1)
 def SetaccName(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5003, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def SetaccValue(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5004, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def accChild(self, varChild=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(-5002, LCID, 2, (9, 0), ((12, 1),),varChild
			)

		if ret is not None:

			ret = Dispatch(ret, 'accChild', None, UnicodeToString=0)

		return ret
 def accDoDefaultAction(self, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5018, LCID, 1, (24, 0), ((12, 17),),varChild
			)
 def accHitTest(self, xLeft=defaultNamedNotOptArg, yTop=defaultNamedNotOptArg):

		return self._ApplyTypes_(-5017, 1, (12, 0), ((3, 1), (3, 1)), 'accHitTest', None,xLeft
			, yTop)
 def accLocation(self, pxLeft=pythoncom.Missing, pyTop=pythoncom.Missing, pcxWidth=pythoncom.Missing, pcyHeight=pythoncom.Missing
			, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5015, 1, (24, 0), ((16387, 2), (16387, 2), (16387, 2), (16387, 2), (12, 17)), 'accLocation', None,pxLeft
			, pyTop, pcxWidth, pcyHeight, varChild)
 def accNavigate(self, navDir=defaultNamedNotOptArg, varStart=defaultNamedOptArg):

		return self._ApplyTypes_(-5016, 1, (12, 0), ((3, 1), (12, 17)), 'accNavigate', None,navDir
			, varStart)
 def accSelect(self, flagsSelect=defaultNamedNotOptArg, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5014, LCID, 1, (24, 0), ((3, 1), (12, 17)),flagsSelect
			, varChild)

	_prop_map_get_ = {
		"Application": (1610809344, 2, (9, 0), (), "Application", None),
		"BeginGroup": (1610874880, 2, (11, 0), (), "BeginGroup", None),
		"BuiltIn": (1610874882, 2, (11, 0), (), "BuiltIn", None),
		"Caption": (1610874883, 2, (8, 0), (), "Caption", None),
		"Control": (1610874885, 2, (9, 0), (), "Control", None),
		"Creator": (1610809345, 2, (3, 0), (), "Creator", None),
		"DescriptionText": (1610874888, 2, (8, 0), (), "DescriptionText", None),
		"DropDownLines": (1610940418, 2, (3, 0), (), "DropDownLines", None),
		"DropDownWidth": (1610940420, 2, (3, 0), (), "DropDownWidth", None),
		"Enabled": (1610874890, 2, (11, 0), (), "Enabled", None),
		"Height": (1610874893, 2, (3, 0), (), "Height", None),
		"HelpContextId": (1610874895, 2, (3, 0), (), "HelpContextId", None),
		"HelpFile": (1610874897, 2, (8, 0), (), "HelpFile", None),
		"Id": (1610874899, 2, (3, 0), (), "Id", None),
		"Index": (1610874900, 2, (3, 0), (), "Index", None),
		"InstanceId": (1610874901, 2, (3, 0), (), "InstanceId", None),
		"IsPriorityDropped": (1610874925, 2, (11, 0), (), "IsPriorityDropped", None),
		"Left": (1610874903, 2, (3, 0), (), "Left", None),
		"ListCount": (1610940424, 2, (3, 0), (), "ListCount", None),
		"ListHeaderCount": (1610940425, 2, (3, 0), (), "ListHeaderCount", None),
		"ListIndex": (1610940427, 2, (3, 0), (), "ListIndex", None),
		"OLEUsage": (1610874904, 2, (3, 0), (), "OLEUsage", None),
		"OnAction": (1610874906, 2, (8, 0), (), "OnAction", None),
		"Parameter": (1610874909, 2, (8, 0), (), "Parameter", None),
		
		"Parent": (1610874908, 2, (9, 0), (), "Parent", '{000C0304-0000-0000-C000-000000000046}'),
		"Priority": (1610874911, 2, (3, 0), (), "Priority", None),
		"Style": (1610940430, 2, (3, 0), (), "Style", None),
		"Tag": (1610874915, 2, (8, 0), (), "Tag", None),
		"Text": (1610940432, 2, (8, 0), (), "Text", None),
		"TooltipText": (1610874917, 2, (8, 0), (), "TooltipText", None),
		"Top": (1610874919, 2, (3, 0), (), "Top", None),
		"Type": (1610874920, 2, (3, 0), (), "Type", None),
		"Visible": (1610874921, 2, (11, 0), (), "Visible", None),
		"Width": (1610874923, 2, (3, 0), (), "Width", None),
		"accChildCount": (-5001, 2, (3, 0), (), "accChildCount", None),
		"accDefaultAction": (-5013, 2, (8, 0), ((12, 17),), "accDefaultAction", None),
		"accDescription": (-5005, 2, (8, 0), ((12, 17),), "accDescription", None),
		"accFocus": (-5011, 2, (12, 0), (), "accFocus", None),
		"accHelp": (-5008, 2, (8, 0), ((12, 17),), "accHelp", None),
		"accHelpTopic": (-5009, 2, (3, 0), ((16392, 2), (12, 17)), "accHelpTopic", None),
		"accKeyboardShortcut": (-5010, 2, (8, 0), ((12, 17),), "accKeyboardShortcut", None),
		"accName": (-5003, 2, (8, 0), ((12, 17),), "accName", None),
		"accParent": (-5000, 2, (9, 0), (), "accParent", None),
		"accRole": (-5006, 2, (12, 0), ((12, 17),), "accRole", None),
		"accSelection": (-5012, 2, (12, 0), (), "accSelection", None),
		"accState": (-5007, 2, (12, 0), ((12, 17),), "accState", None),
		"accValue": (-5004, 2, (8, 0), ((12, 17),), "accValue", None),
	}
		_prop_map_put_ = {
		"BeginGroup": ((1610874880, LCID, 4, 0),()),
		"Caption": ((1610874883, LCID, 4, 0),()),
		"DescriptionText": ((1610874888, LCID, 4, 0),()),
		"DropDownLines": ((1610940418, LCID, 4, 0),()),
		"DropDownWidth": ((1610940420, LCID, 4, 0),()),
		"Enabled": ((1610874890, LCID, 4, 0),()),
		"Height": ((1610874893, LCID, 4, 0),()),
		"HelpContextId": ((1610874895, LCID, 4, 0),()),
		"HelpFile": ((1610874897, LCID, 4, 0),()),
		"ListHeaderCount": ((1610940425, LCID, 4, 0),()),
		"ListIndex": ((1610940427, LCID, 4, 0),()),
		"OLEUsage": ((1610874904, LCID, 4, 0),()),
		"OnAction": ((1610874906, LCID, 4, 0),()),
		"Parameter": ((1610874909, LCID, 4, 0),()),
		"Priority": ((1610874911, LCID, 4, 0),()),
		"Style": ((1610940430, LCID, 4, 0),()),
		"Tag": ((1610874915, LCID, 4, 0),()),
		"Text": ((1610940432, LCID, 4, 0),()),
		"TooltipText": ((1610874917, LCID, 4, 0),()),
		"Visible": ((1610874921, LCID, 4, 0),()),
		"Width": ((1610874923, LCID, 4, 0),()),
		"accName": ((-5003, LCID, 4, 0),()),
		"accValue": ((-5004, LCID, 4, 0),()),
	}
class  _CommandBarComboBoxEvents :
	CLSID = CLSID_Sink = IID('{000C0354-0000-0000-C000-000000000046}')
		coclass_clsid = IID('{55F88897-7708-11D1-ACEB-006008961DA5}')
		_public_methods_ = []
		_dispid_to_func_ = {
		1610678275 : "OnInvoke",
		1610678273 : "OnGetTypeInfo",
		1610612737 : "OnAddRef",
		1610612736 : "OnQueryInterface",
		1610612738 : "OnRelease",
		1610678274 : "OnGetIDsOfNames",
		1610678272 : "OnGetTypeInfoCount",
		        1 : "OnChange",
		}
		def __init__(self, oobj = None):

		if oobj is None:

			self._olecp = None

		else:

			import win32com.server.util

			from win32com.server.policy import EventHandlerPolicy

			cpc=oobj._oleobj_.QueryInterface(pythoncom.IID_IConnectionPointContainer)

			cp=cpc.FindConnectionPoint(self.CLSID_Sink)

			cookie=cp.Advise(win32com.server.util.wrap(self, usePolicy=EventHandlerPolicy))

			self._olecp,self._olecp_cookie = cp,cookie
 def __del__(self):

		try:

			self.close()

		except pythoncom.com_error:

			pass
 def close(self):

		if self._olecp is not None:

			cp,cookie,self._olecp,self._olecp_cookie = self._olecp,self._olecp_cookie,None,None

			cp.Unadvise(cookie)
 def _query_interface_(self, iid):

		import win32com.server.util

		if iid==self.CLSID_Sink: return win32com.server.util.wrap(self)

class  _CommandBars (DispatchBaseClass) :
	CLSID = IID('{000C0302-0000-0000-C000-000000000046}')
		coclass_clsid = IID('{55F88893-7708-11D1-ACEB-006008961DA5}')
		
	def Add(self, Name=defaultNamedOptArg, Position=defaultNamedOptArg, MenuBar=defaultNamedOptArg, Temporary=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610809346, LCID, 1, (9, 0), ((12, 17), (12, 17), (12, 17), (12, 17)),Name
			, Position, MenuBar, Temporary)

		if ret is not None:

			ret = Dispatch(ret, 'Add', '{000C0304-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def AddEx(self, TbidOrName=defaultNamedOptArg, Position=defaultNamedOptArg, MenuBar=defaultNamedOptArg, Temporary=defaultNamedOptArg
			, TbtrProtection=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610809366, LCID, 1, (9, 0), ((12, 17), (12, 17), (12, 17), (12, 17), (12, 17)),TbidOrName
			, Position, MenuBar, Temporary, TbtrProtection)

		if ret is not None:

			ret = Dispatch(ret, 'AddEx', '{000C0304-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def FindControl(self, Type=defaultNamedOptArg, Id=defaultNamedOptArg, Tag=defaultNamedOptArg, Visible=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610809352, LCID, 1, (9, 0), ((12, 17), (12, 17), (12, 17), (12, 17)),Type
			, Id, Tag, Visible)

		if ret is not None:

			ret = Dispatch(ret, 'FindControl', '{000C0308-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def FindControls(self, Type=defaultNamedOptArg, Id=defaultNamedOptArg, Tag=defaultNamedOptArg, Visible=defaultNamedOptArg):

		ret = self._oleobj_.InvokeTypes(1610809365, LCID, 1, (9, 0), ((12, 17), (12, 17), (12, 17), (12, 17)),Type
			, Id, Tag, Visible)

		if ret is not None:

			ret = Dispatch(ret, 'FindControls', '{000C0306-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def IdsString(self, ids=defaultNamedNotOptArg, pbstrName=pythoncom.Missing):

		return self._ApplyTypes_(1610809361, 2, (3, 0), ((3, 1), (16392, 2)), 'IdsString', None,ids
			, pbstrName)
 def Item(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, 'Item', '{000C0304-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def ReleaseFocus(self):

		return self._oleobj_.InvokeTypes(1610809360, LCID, 1, (24, 0), (),)
 def TmcGetName(self, tmc=defaultNamedNotOptArg, pbstrName=pythoncom.Missing):

		return self._ApplyTypes_(1610809362, 2, (3, 0), ((3, 1), (16392, 2)), 'TmcGetName', None,tmc
			, pbstrName)

	_prop_map_get_ = {
		
		"ActionControl": (1610809344, 2, (9, 0), (), "ActionControl", '{000C0308-0000-0000-C000-000000000046}'),
		
		"ActiveMenuBar": (1610809345, 2, (9, 0), (), "ActiveMenuBar", '{000C0304-0000-0000-C000-000000000046}'),
		"AdaptiveMenus": (1610809363, 2, (11, 0), (), "AdaptiveMenus", None),
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Count": (1610809347, 2, (3, 0), (), "Count", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
		"DisplayFonts": (1610809367, 2, (11, 0), (), "DisplayFonts", None),
		"DisplayKeysInTooltips": (1610809350, 2, (11, 0), (), "DisplayKeysInTooltips", None),
		"DisplayTooltips": (1610809348, 2, (11, 0), (), "DisplayTooltips", None),
		"LargeButtons": (1610809354, 2, (11, 0), (), "LargeButtons", None),
		"MenuAnimationStyle": (1610809356, 2, (3, 0), (), "MenuAnimationStyle", None),
		"Parent": (1610809359, 2, (9, 0), (), "Parent", None),
	}
		_prop_map_put_ = {
		"AdaptiveMenus": ((1610809363, LCID, 4, 0),()),
		"DisplayFonts": ((1610809367, LCID, 4, 0),()),
		"DisplayKeysInTooltips": ((1610809350, LCID, 4, 0),()),
		"DisplayTooltips": ((1610809348, LCID, 4, 0),()),
		"LargeButtons": ((1610809354, LCID, 4, 0),()),
		"MenuAnimationStyle": ((1610809356, LCID, 4, 0),()),
	}
		
	def __call__(self, Index=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(0, LCID, 2, (9, 0), ((12, 1),),Index
			)

		if ret is not None:

			ret = Dispatch(ret, '__call__', '{000C0304-0000-0000-C000-000000000046}', UnicodeToString=0)

		return ret
 def __unicode__(self, *args):

		try:

			return unicode(self.__call__(*args))

		except pythoncom.com_error:

			return repr(self)
 def __str__(self, *args):

		return str(self.__unicode__(*args))
 def __int__(self, *args):

		return int(self.__call__(*args))
 def __iter__(self):

		"Return a Python iterator for this object"

		ob = self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),())

		return win32com.client.util.Iterator(ob)
 def _NewEnum(self):

		"Create an enumerator from this object"

		return win32com.client.util.WrapEnum(self._oleobj_.InvokeTypes(-4,LCID,2,(13, 10),()),'{000C0304-0000-0000-C000-000000000046}')
 def __getitem__(self, index):

		"Allow this class to be accessed as a collection"

		if not self.__dict__.has_key('_enum_'):

			self.__dict__['_enum_'] = self._NewEnum()

		return self._enum_.__getitem__(index)
 def __len__(self):

		return self._ApplyTypes_(*(1610809347, 2, (3, 0), (), "Count", None))
 def __nonzero__(self):

		return True

class  _CommandBarsEvents :
	CLSID = CLSID_Sink = IID('{000C0352-0000-0000-C000-000000000046}')
		coclass_clsid = IID('{55F88893-7708-11D1-ACEB-006008961DA5}')
		_public_methods_ = []
		_dispid_to_func_ = {
		1610678275 : "OnInvoke",
		1610678273 : "OnGetTypeInfo",
		        1 : "OnUpdate",
		1610612737 : "OnAddRef",
		1610612736 : "OnQueryInterface",
		1610612738 : "OnRelease",
		1610678274 : "OnGetIDsOfNames",
		1610678272 : "OnGetTypeInfoCount",
		}
		def __init__(self, oobj = None):

		if oobj is None:

			self._olecp = None

		else:

			import win32com.server.util

			from win32com.server.policy import EventHandlerPolicy

			cpc=oobj._oleobj_.QueryInterface(pythoncom.IID_IConnectionPointContainer)

			cp=cpc.FindConnectionPoint(self.CLSID_Sink)

			cookie=cp.Advise(win32com.server.util.wrap(self, usePolicy=EventHandlerPolicy))

			self._olecp,self._olecp_cookie = cp,cookie
 def __del__(self):

		try:

			self.close()

		except pythoncom.com_error:

			pass
 def close(self):

		if self._olecp is not None:

			cp,cookie,self._olecp,self._olecp_cookie = self._olecp,self._olecp_cookie,None,None

			cp.Unadvise(cookie)
 def _query_interface_(self, iid):

		import win32com.server.util

		if iid==self.CLSID_Sink: return win32com.server.util.wrap(self)

class  _IMsoDispObj (DispatchBaseClass) :
	CLSID = IID('{000C0300-0000-0000-C000-000000000046}')
		coclass_clsid = None
		_prop_map_get_ = {
		"Application": (1610743808, 2, (9, 0), (), "Application", None),
		"Creator": (1610743809, 2, (3, 0), (), "Creator", None),
	}
		_prop_map_put_ = {
	}
class  _IMsoOleAccDispObj (DispatchBaseClass) :
	CLSID = IID('{000C0301-0000-0000-C000-000000000046}')
		coclass_clsid = None
		
	def GetaccDefaultAction(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5013, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccDescription(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5005, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelp(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5008, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccHelpTopic(self, pszHelpFile=pythoncom.Missing, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5009, 2, (3, 0), ((16392, 2), (12, 17)), 'GetaccHelpTopic', None,pszHelpFile
			, varChild)
 def GetaccKeyboardShortcut(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5010, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccName(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5003, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def GetaccRole(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5006, 2, (12, 0), ((12, 17),), 'GetaccRole', None,varChild
			)
 def GetaccState(self, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5007, 2, (12, 0), ((12, 17),), 'GetaccState', None,varChild
			)
 def GetaccValue(self, varChild=defaultNamedOptArg):

		
		return self._oleobj_.InvokeTypes(-5004, LCID, 2, (8, 0), ((12, 17),),varChild
			)
 def SetaccName(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5003, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def SetaccValue(self, varChild=defaultNamedNotOptArg, arg1=defaultUnnamedArg):

		return self._oleobj_.InvokeTypes(-5004, LCID, 4, (24, 0), ((12, 17), (8, 1)),varChild
			, arg1)
 def accChild(self, varChild=defaultNamedNotOptArg):

		ret = self._oleobj_.InvokeTypes(-5002, LCID, 2, (9, 0), ((12, 1),),varChild
			)

		if ret is not None:

			ret = Dispatch(ret, 'accChild', None, UnicodeToString=0)

		return ret
 def accDoDefaultAction(self, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5018, LCID, 1, (24, 0), ((12, 17),),varChild
			)
 def accHitTest(self, xLeft=defaultNamedNotOptArg, yTop=defaultNamedNotOptArg):

		return self._ApplyTypes_(-5017, 1, (12, 0), ((3, 1), (3, 1)), 'accHitTest', None,xLeft
			, yTop)
 def accLocation(self, pxLeft=pythoncom.Missing, pyTop=pythoncom.Missing, pcxWidth=pythoncom.Missing, pcyHeight=pythoncom.Missing
			, varChild=defaultNamedOptArg):

		return self._ApplyTypes_(-5015, 1, (24, 0), ((16387, 2), (16387, 2), (16387, 2), (16387, 2), (12, 17)), 'accLocation', None,pxLeft
			, pyTop, pcxWidth, pcyHeight, varChild)
 def accNavigate(self, navDir=defaultNamedNotOptArg, varStart=defaultNamedOptArg):

		return self._ApplyTypes_(-5016, 1, (12, 0), ((3, 1), (12, 17)), 'accNavigate', None,navDir
			, varStart)
 def accSelect(self, flagsSelect=defaultNamedNotOptArg, varChild=defaultNamedOptArg):

		return self._oleobj_.InvokeTypes(-5014, LCID, 1, (24, 0), ((3, 1), (12, 17)),flagsSelect
			, varChild)

	_prop_map_get_ = {
		"Application": (1610809344, 2, (9, 0), (), "Application", None),
		"Creator": (1610809345, 2, (3, 0), (), "Creator", None),
		"accChildCount": (-5001, 2, (3, 0), (), "accChildCount", None),
		"accDefaultAction": (-5013, 2, (8, 0), ((12, 17),), "accDefaultAction", None),
		"accDescription": (-5005, 2, (8, 0), ((12, 17),), "accDescription", None),
		"accFocus": (-5011, 2, (12, 0), (), "accFocus", None),
		"accHelp": (-5008, 2, (8, 0), ((12, 17),), "accHelp", None),
		"accHelpTopic": (-5009, 2, (3, 0), ((16392, 2), (12, 17)), "accHelpTopic", None),
		"accKeyboardShortcut": (-5010, 2, (8, 0), ((12, 17),), "accKeyboardShortcut", None),
		"accName": (-5003, 2, (8, 0), ((12, 17),), "accName", None),
		"accParent": (-5000, 2, (9, 0), (), "accParent", None),
		"accRole": (-5006, 2, (12, 0), ((12, 17),), "accRole", None),
		"accSelection": (-5012, 2, (12, 0), (), "accSelection", None),
		"accState": (-5007, 2, (12, 0), ((12, 17),), "accState", None),
		"accValue": (-5004, 2, (8, 0), ((12, 17),), "accValue", None),
	}
		_prop_map_put_ = {
		"accName": ((-5003, LCID, 4, 0),()),
		"accValue": ((-5004, LCID, 4, 0),()),
	}
from win32com.client import CoClassBaseClass class  CommandBarButton (CoClassBaseClass) :
	CLSID = IID('{55F88891-7708-11D1-ACEB-006008961DA5}')
		coclass_sources = [
		_CommandBarButtonEvents,
	]
		default_source = _CommandBarButtonEvents
		coclass_interfaces = [
		_CommandBarButton,
	]
		default_interface = _CommandBarButton
class  CommandBarComboBox (CoClassBaseClass) :
	CLSID = IID('{55F88897-7708-11D1-ACEB-006008961DA5}')
		coclass_sources = [
		_CommandBarComboBoxEvents,
	]
		default_source = _CommandBarComboBoxEvents
		coclass_interfaces = [
		_CommandBarComboBox,
	]
		default_interface = _CommandBarComboBox
class  CommandBars (CoClassBaseClass) :
	CLSID = IID('{55F88893-7708-11D1-ACEB-006008961DA5}')
		coclass_sources = [
		_CommandBarsEvents,
	]
		default_source = _CommandBarsEvents
		coclass_interfaces = [
		_CommandBars,
	]
		default_interface = _CommandBars
Adjustments_vtables_dispatch_ = 1 Adjustments_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'Count' , ), 2, (2, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'Val' , ), 0, (0, (), [ (3, 1, None, None) , 
			(16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'Val' , ), 0, (0, (), [ (3, 1, None, None) , 
			(4, 1, None, None) , ], 1 , 4 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
] AnswerWizard_vtables_dispatch_ = 1 AnswerWizard_vtables_ = [
	(( 'Parent' , 'ppidisp' , ), 1610809344, (1610809344, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Files' , 'Files' , ), 1610809345, (1610809345, (), [ (16393, 10, None, "IID('{000C0361-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'ClearFileList' , ), 1610809346, (1610809346, (), [ ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'ResetFileList' , ), 1610809347, (1610809347, (), [ ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
] AnswerWizardFiles_vtables_dispatch_ = 1 AnswerWizardFiles_vtables_ = [
	(( 'Parent' , 'ppidisp' , ), 1610809344, (1610809344, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'pbstr' , ), 0, (0, (), [ (3, 1, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pCount' , ), 1610809346, (1610809346, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Add' , 'FileName' , ), 1610809347, (1610809347, (), [ (8, 1, None, None) , ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Delete' , 'FileName' , ), 1610809348, (1610809348, (), [ (8, 1, None, None) , ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
] Assistant_vtables_dispatch_ = 1 Assistant_vtables_ = [
	(( 'Parent' , 'ppidisp' , ), 1610809344, (1610809344, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Move' , 'xLeft' , 'yTop' , ), 1610809345, (1610809345, (), [ (3, 1, None, None) , 
			(3, 1, None, None) , ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Top' , 'pyTop' , ), 1610809346, (1610809346, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Top' , 'pyTop' , ), 1610809346, (1610809346, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Left' , 'pxLeft' , ), 1610809348, (1610809348, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Left' , 'pxLeft' , ), 1610809348, (1610809348, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Help' , ), 1610809350, (1610809350, (), [ ], 1 , 1 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'StartWizard' , 'On' , 'Callback' , 'PrivateX' , 'Animation' , 
			'CustomTeaser' , 'Top' , 'Left' , 'Bottom' , 'Right' , 
			'plWizID' , ), 1610809351, (1610809351, (), [ (11, 1, None, None) , (8, 1, None, None) , (3, 1, None, None) , 
			(12, 17, None, None) , (12, 17, None, None) , (12, 17, None, None) , (12, 17, None, None) , (12, 17, None, None) , 
			(12, 17, None, None) , (16387, 10, None, None) , ], 1 , 1 , 4 , 6 , 64 , (3, 0, None, None) , 0 , )),
	(( 'EndWizard' , 'WizardID' , 'varfSuccess' , 'Animation' , ), 1610809352, (1610809352, (), [ 
			(3, 1, None, None) , (11, 1, None, None) , (12, 17, None, None) , ], 1 , 1 , 4 , 1 , 68 , (3, 0, None, None) , 0 , )),
	(( 'ActivateWizard' , 'WizardID' , 'act' , 'Animation' , ), 1610809353, (1610809353, (), [ 
			(3, 1, None, None) , (3, 1, None, None) , (12, 17, None, None) , ], 1 , 1 , 4 , 1 , 72 , (3, 0, None, None) , 0 , )),
	(( 'ResetTips' , ), 1610809354, (1610809354, (), [ ], 1 , 1 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'NewBalloon' , 'ppibal' , ), 1610809355, (1610809355, (), [ (16393, 10, None, "IID('{000C0324-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'BalloonError' , 'pbne' , ), 1610809356, (1610809356, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'pvarfVisible' , ), 1610809357, (1610809357, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'pvarfVisible' , ), 1610809357, (1610809357, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'Animation' , 'pfca' , ), 1610809359, (1610809359, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'Animation' , 'pfca' , ), 1610809359, (1610809359, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'Reduced' , 'pvarfReduced' , ), 1610809361, (1610809361, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'Reduced' , 'pvarfReduced' , ), 1610809361, (1610809361, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'AssistWithHelp' , 'pvarfAssistWithHelp' , ), 1610809363, (1610809363, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'AssistWithHelp' , 'pvarfAssistWithHelp' , ), 1610809363, (1610809363, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'AssistWithWizards' , 'pvarfAssistWithWizards' , ), 1610809365, (1610809365, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'AssistWithWizards' , 'pvarfAssistWithWizards' , ), 1610809365, (1610809365, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'AssistWithAlerts' , 'pvarfAssistWithAlerts' , ), 1610809367, (1610809367, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'AssistWithAlerts' , 'pvarfAssistWithAlerts' , ), 1610809367, (1610809367, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'MoveWhenInTheWay' , 'pvarfMove' , ), 1610809369, (1610809369, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
	(( 'MoveWhenInTheWay' , 'pvarfMove' , ), 1610809369, (1610809369, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 140 , (3, 0, None, None) , 0 , )),
	(( 'Sounds' , 'pvarfSounds' , ), 1610809371, (1610809371, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 144 , (3, 0, None, None) , 0 , )),
	(( 'Sounds' , 'pvarfSounds' , ), 1610809371, (1610809371, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 148 , (3, 0, None, None) , 0 , )),
	(( 'FeatureTips' , 'pvarfFeatures' , ), 1610809373, (1610809373, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 152 , (3, 0, None, None) , 0 , )),
	(( 'FeatureTips' , 'pvarfFeatures' , ), 1610809373, (1610809373, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 156 , (3, 0, None, None) , 0 , )),
	(( 'MouseTips' , 'pvarfMouse' , ), 1610809375, (1610809375, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 160 , (3, 0, None, None) , 0 , )),
	(( 'MouseTips' , 'pvarfMouse' , ), 1610809375, (1610809375, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 164 , (3, 0, None, None) , 0 , )),
	(( 'KeyboardShortcutTips' , 'pvarfKeyboardShortcuts' , ), 1610809377, (1610809377, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 168 , (3, 0, None, None) , 0 , )),
	(( 'KeyboardShortcutTips' , 'pvarfKeyboardShortcuts' , ), 1610809377, (1610809377, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 172 , (3, 0, None, None) , 0 , )),
	(( 'HighPriorityTips' , 'pvarfHighPriorityTips' , ), 1610809379, (1610809379, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 176 , (3, 0, None, None) , 0 , )),
	(( 'HighPriorityTips' , 'pvarfHighPriorityTips' , ), 1610809379, (1610809379, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 180 , (3, 0, None, None) , 0 , )),
	(( 'TipOfDay' , 'pvarfTipOfDay' , ), 1610809381, (1610809381, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 184 , (3, 0, None, None) , 0 , )),
	(( 'TipOfDay' , 'pvarfTipOfDay' , ), 1610809381, (1610809381, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 188 , (3, 0, None, None) , 0 , )),
	(( 'GuessHelp' , 'pvarfGuessHelp' , ), 1610809383, (1610809383, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 192 , (3, 0, None, None) , 0 , )),
	(( 'GuessHelp' , 'pvarfGuessHelp' , ), 1610809383, (1610809383, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 196 , (3, 0, None, None) , 0 , )),
	(( 'SearchWhenProgramming' , 'pvarfSearchInProgram' , ), 1610809385, (1610809385, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 200 , (3, 0, None, None) , 0 , )),
	(( 'SearchWhenProgramming' , 'pvarfSearchInProgram' , ), 1610809385, (1610809385, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 204 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'pbstrName' , ), 0, (0, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 208 , (3, 0, None, None) , 0 , )),
	(( 'FileName' , 'pbstr' , ), 1610809388, (1610809388, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 212 , (3, 0, None, None) , 0 , )),
	(( 'FileName' , 'pbstr' , ), 1610809388, (1610809388, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 216 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'pbstrName' , ), 1610809390, (1610809390, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 220 , (3, 0, None, None) , 0 , )),
	(( 'On' , 'pvarfOn' , ), 1610809391, (1610809391, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 224 , (3, 0, None, None) , 0 , )),
	(( 'On' , 'pvarfOn' , ), 1610809391, (1610809391, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 228 , (3, 0, None, None) , 0 , )),
] Balloon_vtables_dispatch_ = 1 Balloon_vtables_ = [
	(( 'Parent' , 'ppidisp' , ), 1610809344, (1610809344, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Checkboxes' , 'ppidisp' , ), 1610809345, (1610809345, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Labels' , 'ppidisp' , ), 1610809346, (1610809346, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'BalloonType' , 'pbty' , ), 1610809347, (1610809347, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'BalloonType' , 'pbty' , ), 1610809347, (1610809347, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Icon' , 'picn' , ), 1610809349, (1610809349, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Icon' , 'picn' , ), 1610809349, (1610809349, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'Heading' , 'pbstr' , ), 1610809351, (1610809351, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'Heading' , 'pbstr' , ), 1610809351, (1610809351, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstr' , ), 1610809353, (1610809353, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstr' , ), 1610809353, (1610809353, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'Mode' , 'pmd' , ), 1610809355, (1610809355, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'Mode' , 'pmd' , ), 1610809355, (1610809355, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'Animation' , 'pfca' , ), 1610809357, (1610809357, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'Animation' , 'pfca' , ), 1610809357, (1610809357, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'Button' , 'psbs' , ), 1610809359, (1610809359, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'Button' , 'psbs' , ), 1610809359, (1610809359, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'Callback' , 'pbstr' , ), 1610809361, (1610809361, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'Callback' , 'pbstr' , ), 1610809361, (1610809361, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'Private' , 'plPrivate' , ), 1610809363, (1610809363, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'Private' , 'plPrivate' , ), 1610809363, (1610809363, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'SetAvoidRectangle' , 'Left' , 'Top' , 'Right' , 'Bottom' , 
			), 1610809365, (1610809365, (), [ (3, 1, None, None) , (3, 1, None, None) , (3, 1, None, None) , (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'pbstrName' , ), 1610809366, (1610809366, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'Show' , 'pibtn' , ), 1610809367, (1610809367, (), [ (16387, 10, None, None) , ], 1 , 1 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'Close' , ), 1610809368, (1610809368, (), [ ], 1 , 1 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
] BalloonCheckbox_vtables_dispatch_ = 1 BalloonCheckbox_vtables_ = [
	(( 'Item' , 'pbstrName' , ), 0, (0, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'pbstrName' , ), 1610809345, (1610809345, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'ppidisp' , ), 1610809346, (1610809346, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Checked' , 'pvarfChecked' , ), 1610809347, (1610809347, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Checked' , 'pvarfChecked' , ), 1610809347, (1610809347, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstr' , ), 1610809349, (1610809349, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstr' , ), 1610809349, (1610809349, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
] BalloonCheckboxes_vtables_dispatch_ = 1 BalloonCheckboxes_vtables_ = [
	(( 'Name' , 'pbstrName' , ), 1610809344, (1610809344, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'ppidisp' , ), 1610809345, (1610809345, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'ppidisp' , ), 0, (0, (), [ (3, 1, None, None) , 
			(16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pccbx' , ), 1610809347, (1610809347, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pccbx' , ), 1610809347, (1610809347, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'ppienum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 1024 , )),
] BalloonLabel_vtables_dispatch_ = 1 BalloonLabel_vtables_ = [
	(( 'Item' , 'pbstrName' , ), 0, (0, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'pbstrName' , ), 1610809345, (1610809345, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'ppidisp' , ), 1610809346, (1610809346, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstr' , ), 1610809347, (1610809347, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstr' , ), 1610809347, (1610809347, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
] BalloonLabels_vtables_dispatch_ = 1 BalloonLabels_vtables_ = [
	(( 'Name' , 'pbstrName' , ), 1610809344, (1610809344, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'ppidisp' , ), 1610809345, (1610809345, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'ppidisp' , ), 0, (0, (), [ (3, 1, None, None) , 
			(16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pcwz' , ), 1610809347, (1610809347, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pcwz' , ), 1610809347, (1610809347, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'ppienum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 1024 , )),
] COMAddIn_vtables_dispatch_ = 1 COMAddIn_vtables_ = [
	(( 'Description' , 'RetValue' , ), 0, (0, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Description' , 'RetValue' , ), 0, (0, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'ProgId' , 'RetValue' , ), 3, (3, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Guid' , 'RetValue' , ), 4, (4, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Connect' , 'RetValue' , ), 6, (6, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Connect' , 'RetValue' , ), 6, (6, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Object' , 'RetValue' , ), 7, (7, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'Object' , 'RetValue' , ), 7, (7, (), [ (9, 1, None, None) , ], 1 , 4 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'retval' , ), 8, (8, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
] COMAddIns_vtables_dispatch_ = 1 COMAddIns_vtables_ = [
	(( 'Item' , 'Index' , 'RetValue' , ), 0, (0, (), [ (16396, 1, None, None) , 
			(16393, 10, None, "IID('{000C033A-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'RetValue' , ), 1, (1, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'RetValue' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 1025 , )),
	(( 'Update' , ), 2, (2, (), [ ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'ppidisp' , ), 3, (3, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'SetAppModal' , 'varfModal' , ), 4, (4, (), [ (11, 1, None, None) , ], 1 , 1 , 4 , 0 , 56 , (3, 0, None, None) , 1088 , )),
] CalloutFormat_vtables_dispatch_ = 1 CalloutFormat_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'AutomaticLength' , ), 10, (10, (), [ ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'CustomDrop' , 'Drop' , ), 11, (11, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'CustomLength' , 'Length' , ), 12, (12, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'PresetDrop' , 'DropType' , ), 13, (13, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Accent' , 'Accent' , ), 100, (100, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Accent' , 'Accent' , ), 100, (100, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'Angle' , 'Angle' , ), 101, (101, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'Angle' , 'Angle' , ), 101, (101, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'AutoAttach' , 'AutoAttach' , ), 102, (102, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'AutoAttach' , 'AutoAttach' , ), 102, (102, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'AutoLength' , 'AutoLength' , ), 103, (103, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'Border' , 'Border' , ), 104, (104, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'Border' , 'Border' , ), 104, (104, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'Drop' , 'Drop' , ), 105, (105, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'DropType' , 'DropType' , ), 106, (106, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'Gap' , 'Gap' , ), 107, (107, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'Gap' , 'Gap' , ), 107, (107, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'Length' , 'Length' , ), 108, (108, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 109, (109, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 109, (109, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
] ColorFormat_vtables_dispatch_ = 1 ColorFormat_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'RGB' , 'RGB' , ), 0, (0, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'RGB' , 'RGB' , ), 0, (0, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'SchemeColor' , 'SchemeColor' , ), 100, (100, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'SchemeColor' , 'SchemeColor' , ), 100, (100, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 101, (101, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
] CommandBar_vtables_dispatch_ = 1 CommandBar_vtables_ = [
	(( 'BuiltIn' , 'pvarfBuiltIn' , ), 1610874880, (1610874880, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'Context' , 'pbstrContext' , ), 1610874881, (1610874881, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'Context' , 'pbstrContext' , ), 1610874881, (1610874881, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'Controls' , 'ppcbcs' , ), 1610874883, (1610874883, (), [ (16393, 10, None, "IID('{000C0306-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'Delete' , ), 1610874884, (1610874884, (), [ ], 1 , 1 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
	(( 'Enabled' , 'pvarfEnabled' , ), 1610874885, (1610874885, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 140 , (3, 0, None, None) , 0 , )),
	(( 'Enabled' , 'pvarfEnabled' , ), 1610874885, (1610874885, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 144 , (3, 0, None, None) , 0 , )),
	(( 'FindControl' , 'Type' , 'Id' , 'Tag' , 'Visible' , 
			'Recursive' , 'ppcbc' , ), 1610874887, (1610874887, (), [ (12, 17, None, None) , (12, 17, None, None) , 
			(12, 17, None, None) , (12, 17, None, None) , (12, 17, None, None) , (16393, 10, None, "IID('{000C0308-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 5 , 148 , (3, 0, None, None) , 0 , )),
	(( 'Height' , 'pdy' , ), 1610874888, (1610874888, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 152 , (3, 0, None, None) , 0 , )),
	(( 'Height' , 'pdy' , ), 1610874888, (1610874888, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 156 , (3, 0, None, None) , 0 , )),
	(( 'Index' , 'pi' , ), 1610874890, (1610874890, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 160 , (3, 0, None, None) , 0 , )),
	(( 'InstanceId' , 'pid' , ), 1610874891, (1610874891, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 164 , (3, 0, None, None) , 64 , )),
	(( 'Left' , 'pxpLeft' , ), 1610874892, (1610874892, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 168 , (3, 0, None, None) , 0 , )),
	(( 'Left' , 'pxpLeft' , ), 1610874892, (1610874892, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 172 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'pbstrName' , ), 1610874894, (1610874894, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 176 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'pbstrName' , ), 1610874894, (1610874894, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 180 , (3, 0, None, None) , 0 , )),
	(( 'NameLocal' , 'pbstrNameLocal' , ), 1610874896, (1610874896, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 184 , (3, 0, None, None) , 0 , )),
	(( 'NameLocal' , 'pbstrNameLocal' , ), 1610874896, (1610874896, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 188 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'ppidisp' , ), 1610874898, (1610874898, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 192 , (3, 0, None, None) , 0 , )),
	(( 'Position' , 'ppos' , ), 1610874899, (1610874899, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 196 , (3, 0, None, None) , 0 , )),
	(( 'Position' , 'ppos' , ), 1610874899, (1610874899, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 200 , (3, 0, None, None) , 0 , )),
	(( 'RowIndex' , 'piRow' , ), 1610874901, (1610874901, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 204 , (3, 0, None, None) , 0 , )),
	(( 'RowIndex' , 'piRow' , ), 1610874901, (1610874901, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 208 , (3, 0, None, None) , 0 , )),
	(( 'Protection' , 'pprot' , ), 1610874903, (1610874903, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 212 , (3, 0, None, None) , 0 , )),
	(( 'Protection' , 'pprot' , ), 1610874903, (1610874903, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 216 , (3, 0, None, None) , 0 , )),
	(( 'Reset' , ), 1610874905, (1610874905, (), [ ], 1 , 1 , 4 , 0 , 220 , (3, 0, None, None) , 0 , )),
	(( 'ShowPopup' , 'x' , 'y' , ), 1610874906, (1610874906, (), [ (12, 17, None, None) , 
			(12, 17, None, None) , ], 1 , 1 , 4 , 2 , 224 , (3, 0, None, None) , 0 , )),
	(( 'Top' , 'pypTop' , ), 1610874907, (1610874907, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 228 , (3, 0, None, None) , 0 , )),
	(( 'Top' , 'pypTop' , ), 1610874907, (1610874907, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 232 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'ptype' , ), 1610874909, (1610874909, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 236 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'pvarfVisible' , ), 1610874910, (1610874910, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 240 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'pvarfVisible' , ), 1610874910, (1610874910, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 244 , (3, 0, None, None) , 0 , )),
	(( 'Width' , 'pdx' , ), 1610874912, (1610874912, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 248 , (3, 0, None, None) , 0 , )),
	(( 'Width' , 'pdx' , ), 1610874912, (1610874912, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 252 , (3, 0, None, None) , 0 , )),
	(( 'AdaptiveMenu' , 'pvarfAdaptiveMenu' , ), 1610874914, (1610874914, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 256 , (3, 0, None, None) , 0 , )),
	(( 'AdaptiveMenu' , 'pvarfAdaptiveMenu' , ), 1610874914, (1610874914, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 260 , (3, 0, None, None) , 0 , )),
] CommandBarControl_vtables_dispatch_ = 1 CommandBarControl_vtables_ = [
	(( 'BeginGroup' , 'pvarfBeginGroup' , ), 1610874880, (1610874880, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'BeginGroup' , 'pvarfBeginGroup' , ), 1610874880, (1610874880, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'BuiltIn' , 'pvarfBuiltIn' , ), 1610874882, (1610874882, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'Caption' , 'pbstrCaption' , ), 1610874883, (1610874883, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'Caption' , 'pbstrCaption' , ), 1610874883, (1610874883, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
	(( 'Control' , 'ppidisp' , ), 1610874885, (1610874885, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 140 , (3, 0, None, None) , 64 , )),
	(( 'Copy' , 'Bar' , 'Before' , 'ppcbc' , ), 1610874886, (1610874886, (), [ 
			(12, 17, None, None) , (12, 17, None, None) , (16393, 10, None, "IID('{000C0308-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 2 , 144 , (3, 0, None, None) , 0 , )),
	(( 'Delete' , 'Temporary' , ), 1610874887, (1610874887, (), [ (12, 17, None, None) , ], 1 , 1 , 4 , 1 , 148 , (3, 0, None, None) , 0 , )),
	(( 'DescriptionText' , 'pbstrText' , ), 1610874888, (1610874888, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 152 , (3, 0, None, None) , 0 , )),
	(( 'DescriptionText' , 'pbstrText' , ), 1610874888, (1610874888, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 156 , (3, 0, None, None) , 0 , )),
	(( 'Enabled' , 'pvarfEnabled' , ), 1610874890, (1610874890, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 160 , (3, 0, None, None) , 0 , )),
	(( 'Enabled' , 'pvarfEnabled' , ), 1610874890, (1610874890, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 164 , (3, 0, None, None) , 0 , )),
	(( 'Execute' , ), 1610874892, (1610874892, (), [ ], 1 , 1 , 4 , 0 , 168 , (3, 0, None, None) , 0 , )),
	(( 'Height' , 'pdy' , ), 1610874893, (1610874893, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 172 , (3, 0, None, None) , 0 , )),
	(( 'Height' , 'pdy' , ), 1610874893, (1610874893, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 176 , (3, 0, None, None) , 0 , )),
	(( 'HelpContextId' , 'pid' , ), 1610874895, (1610874895, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 180 , (3, 0, None, None) , 0 , )),
	(( 'HelpContextId' , 'pid' , ), 1610874895, (1610874895, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 184 , (3, 0, None, None) , 0 , )),
	(( 'HelpFile' , 'pbstrFilename' , ), 1610874897, (1610874897, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 188 , (3, 0, None, None) , 0 , )),
	(( 'HelpFile' , 'pbstrFilename' , ), 1610874897, (1610874897, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 192 , (3, 0, None, None) , 0 , )),
	(( 'Id' , 'pid' , ), 1610874899, (1610874899, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 196 , (3, 0, None, None) , 0 , )),
	(( 'Index' , 'pi' , ), 1610874900, (1610874900, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 200 , (3, 0, None, None) , 0 , )),
	(( 'InstanceId' , 'pid' , ), 1610874901, (1610874901, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 204 , (3, 0, None, None) , 64 , )),
	(( 'Move' , 'Bar' , 'Before' , 'ppcbc' , ), 1610874902, (1610874902, (), [ 
			(12, 17, None, None) , (12, 17, None, None) , (16393, 10, None, "IID('{000C0308-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 2 , 208 , (3, 0, None, None) , 0 , )),
	(( 'Left' , 'px' , ), 1610874903, (1610874903, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 212 , (3, 0, None, None) , 0 , )),
	(( 'OLEUsage' , 'pcou' , ), 1610874904, (1610874904, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 216 , (3, 0, None, None) , 0 , )),
	(( 'OLEUsage' , 'pcou' , ), 1610874904, (1610874904, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 220 , (3, 0, None, None) , 0 , )),
	(( 'OnAction' , 'pbstrOnAction' , ), 1610874906, (1610874906, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 224 , (3, 0, None, None) , 0 , )),
	(( 'OnAction' , 'pbstrOnAction' , ), 1610874906, (1610874906, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 228 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'ppcb' , ), 1610874908, (1610874908, (), [ (16393, 10, None, "IID('{000C0304-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 232 , (3, 0, None, None) , 0 , )),
	(( 'Parameter' , 'pbstrParam' , ), 1610874909, (1610874909, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 236 , (3, 0, None, None) , 0 , )),
	(( 'Parameter' , 'pbstrParam' , ), 1610874909, (1610874909, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 240 , (3, 0, None, None) , 0 , )),
	(( 'Priority' , 'pnPri' , ), 1610874911, (1610874911, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 244 , (3, 0, None, None) , 0 , )),
	(( 'Priority' , 'pnPri' , ), 1610874911, (1610874911, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 248 , (3, 0, None, None) , 0 , )),
	(( 'Reset' , ), 1610874913, (1610874913, (), [ ], 1 , 1 , 4 , 0 , 252 , (3, 0, None, None) , 0 , )),
	(( 'SetFocus' , ), 1610874914, (1610874914, (), [ ], 1 , 1 , 4 , 0 , 256 , (3, 0, None, None) , 0 , )),
	(( 'Tag' , 'pbstrTag' , ), 1610874915, (1610874915, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 260 , (3, 0, None, None) , 0 , )),
	(( 'Tag' , 'pbstrTag' , ), 1610874915, (1610874915, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 264 , (3, 0, None, None) , 0 , )),
	(( 'TooltipText' , 'pbstrTooltip' , ), 1610874917, (1610874917, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 268 , (3, 0, None, None) , 0 , )),
	(( 'TooltipText' , 'pbstrTooltip' , ), 1610874917, (1610874917, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 272 , (3, 0, None, None) , 0 , )),
	(( 'Top' , 'py' , ), 1610874919, (1610874919, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 276 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'ptype' , ), 1610874920, (1610874920, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 280 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'pvarfVisible' , ), 1610874921, (1610874921, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 284 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'pvarfVisible' , ), 1610874921, (1610874921, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 288 , (3, 0, None, None) , 0 , )),
	(( 'Width' , 'pdx' , ), 1610874923, (1610874923, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 292 , (3, 0, None, None) , 0 , )),
	(( 'Width' , 'pdx' , ), 1610874923, (1610874923, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 296 , (3, 0, None, None) , 0 , )),
	(( 'IsPriorityDropped' , 'pvarfDropped' , ), 1610874925, (1610874925, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 300 , (3, 0, None, None) , 0 , )),
	(( 'Reserved1' , ), 1610874926, (1610874926, (), [ ], 1 , 1 , 4 , 0 , 304 , (3, 0, None, None) , 64 , )),
	(( 'Reserved2' , ), 1610874927, (1610874927, (), [ ], 1 , 1 , 4 , 0 , 308 , (3, 0, None, None) , 64 , )),
	(( 'Reserved3' , ), 1610874928, (1610874928, (), [ ], 1 , 1 , 4 , 0 , 312 , (3, 0, None, None) , 64 , )),
	(( 'Reserved4' , ), 1610874929, (1610874929, (), [ ], 1 , 1 , 4 , 0 , 316 , (3, 0, None, None) , 64 , )),
	(( 'Reserved5' , ), 1610874930, (1610874930, (), [ ], 1 , 1 , 4 , 0 , 320 , (3, 0, None, None) , 64 , )),
	(( 'Reserved6' , ), 1610874931, (1610874931, (), [ ], 1 , 1 , 4 , 0 , 324 , (3, 0, None, None) , 64 , )),
	(( 'Reserved7' , ), 1610874932, (1610874932, (), [ ], 1 , 1 , 4 , 0 , 328 , (3, 0, None, None) , 64 , )),
] CommandBarControls_vtables_dispatch_ = 1 CommandBarControls_vtables_ = [
	(( 'Add' , 'Type' , 'Id' , 'Parameter' , 'Before' , 
			'Temporary' , 'ppcbc' , ), 1610809344, (1610809344, (), [ (12, 17, None, None) , (12, 17, None, None) , 
			(12, 17, None, None) , (12, 17, None, None) , (12, 17, None, None) , (16393, 10, None, "IID('{000C0308-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 5 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pcToolbarControls' , ), 1610809345, (1610809345, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'ppcbc' , ), 0, (0, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C0308-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'ppienum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 1024 , )),
	(( 'Parent' , 'ppcb' , ), 1610809348, (1610809348, (), [ (16393, 10, None, "IID('{000C0304-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
] CommandBarPopup_vtables_dispatch_ = 1 CommandBarPopup_vtables_ = [
	(( 'CommandBar' , 'ppcb' , ), 1610940416, (1610940416, (), [ (16393, 10, None, "IID('{000C0304-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 332 , (3, 0, None, None) , 0 , )),
	(( 'Controls' , 'ppcbcs' , ), 1610940417, (1610940417, (), [ (16393, 10, None, "IID('{000C0306-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 336 , (3, 0, None, None) , 0 , )),
	(( 'OLEMenuGroup' , 'pomg' , ), 1610940418, (1610940418, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 340 , (3, 0, None, None) , 0 , )),
	(( 'OLEMenuGroup' , 'pomg' , ), 1610940418, (1610940418, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 344 , (3, 0, None, None) , 0 , )),
] ConnectorFormat_vtables_dispatch_ = 1 ConnectorFormat_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'BeginConnect' , 'ConnectedShape' , 'ConnectionSite' , ), 10, (10, (), [ (9, 1, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , 
			(3, 1, None, None) , ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'BeginDisconnect' , ), 11, (11, (), [ ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'EndConnect' , 'ConnectedShape' , 'ConnectionSite' , ), 12, (12, (), [ (9, 1, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , 
			(3, 1, None, None) , ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'EndDisconnect' , ), 13, (13, (), [ ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'BeginConnected' , 'BeginConnected' , ), 100, (100, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'BeginConnectedShape' , 'BeginConnectedShape' , ), 101, (101, (), [ (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'BeginConnectionSite' , 'BeginConnectionSite' , ), 102, (102, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'EndConnected' , 'EndConnected' , ), 103, (103, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'EndConnectedShape' , 'EndConnectedShape' , ), 104, (104, (), [ (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'EndConnectionSite' , 'EndConnectionSite' , ), 105, (105, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 106, (106, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 106, (106, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
] FileSearch_vtables_dispatch_ = 1 FileSearch_vtables_ = [
	(( 'SearchSubFolders' , 'SearchSubFoldersRetVal' , ), 1, (1, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'SearchSubFolders' , 'SearchSubFoldersRetVal' , ), 1, (1, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'MatchTextExactly' , 'MatchTextRetVal' , ), 2, (2, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'MatchTextExactly' , 'MatchTextRetVal' , ), 2, (2, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'MatchAllWordForms' , 'MatchAllWordFormsRetVal' , ), 3, (3, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'MatchAllWordForms' , 'MatchAllWordFormsRetVal' , ), 3, (3, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'FileName' , 'FileNameRetVal' , ), 4, (4, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'FileName' , 'FileNameRetVal' , ), 4, (4, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'FileType' , 'FileTypeRetVal' , ), 5, (5, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'FileType' , 'FileTypeRetVal' , ), 5, (5, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'LastModified' , 'LastModifiedRetVal' , ), 6, (6, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'LastModified' , 'LastModifiedRetVal' , ), 6, (6, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'TextOrProperty' , 'TextOrProperty' , ), 7, (7, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'TextOrProperty' , 'TextOrProperty' , ), 7, (7, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'LookIn' , 'LookInRetVal' , ), 8, (8, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'LookIn' , 'LookInRetVal' , ), 8, (8, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'Execute' , 'SortBy' , 'SortOrder' , 'AlwaysAccurate' , 'pRet' , 
			), 9, (9, (), [ (3, 49, '1', None) , (3, 49, '1', None) , (11, 49, 'True', None) , (16387, 10, None, None) , ], 1 , 1 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'NewSearch' , ), 10, (10, (), [ ], 1 , 1 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'FoundFiles' , 'FoundFilesRet' , ), 11, (11, (), [ (16393, 10, None, "IID('{000C0331-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'PropertyTests' , 'PropTestsRet' , ), 12, (12, (), [ (16393, 10, None, "IID('{000C0334-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
] FillFormat_vtables_dispatch_ = 1 FillFormat_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Background' , ), 10, (10, (), [ ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'OneColorGradient' , 'Style' , 'Variant' , 'Degree' , ), 11, (11, (), [ 
			(3, 1, None, None) , (3, 1, None, None) , (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Patterned' , 'Pattern' , ), 12, (12, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'PresetGradient' , 'Style' , 'Variant' , 'PresetGradientType' , ), 13, (13, (), [ 
			(3, 1, None, None) , (3, 1, None, None) , (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'PresetTextured' , 'PresetTexture' , ), 14, (14, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Solid' , ), 15, (15, (), [ ], 1 , 1 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'TwoColorGradient' , 'Style' , 'Variant' , ), 16, (16, (), [ (3, 1, None, None) , 
			(3, 1, None, None) , ], 1 , 1 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'UserPicture' , 'PictureFile' , ), 17, (17, (), [ (8, 1, None, None) , ], 1 , 1 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'UserTextured' , 'TextureFile' , ), 18, (18, (), [ (8, 1, None, None) , ], 1 , 1 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'BackColor' , 'BackColor' , ), 100, (100, (), [ (16393, 10, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'BackColor' , 'BackColor' , ), 100, (100, (), [ (9, 1, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 4 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'ForeColor' , 'ForeColor' , ), 101, (101, (), [ (16393, 10, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'ForeColor' , 'ForeColor' , ), 101, (101, (), [ (9, 1, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 4 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'GradientColorType' , 'GradientColorType' , ), 102, (102, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'GradientDegree' , 'GradientDegree' , ), 103, (103, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'GradientStyle' , 'GradientStyle' , ), 104, (104, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'GradientVariant' , 'GradientVariant' , ), 105, (105, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'Pattern' , 'Pattern' , ), 106, (106, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'PresetGradientType' , 'PresetGradientType' , ), 107, (107, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'PresetTexture' , 'PresetTexture' , ), 108, (108, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'TextureName' , 'TextureName' , ), 109, (109, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'TextureType' , 'TextureType' , ), 110, (110, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'Transparency' , 'Transparency' , ), 111, (111, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'Transparency' , 'Transparency' , ), 111, (111, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 112, (112, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 113, (113, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 140 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 113, (113, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 144 , (3, 0, None, None) , 0 , )),
] FoundFiles_vtables_dispatch_ = 1 FoundFiles_vtables_ = [
	(( 'Item' , 'Index' , 'lcid' , 'pbstrFile' , ), 0, (0, (), [ 
			(3, 1, None, None) , (3, 5, None, None) , (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pc' , ), 4, (4, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'ppunkEnum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 1024 , )),
] FreeformBuilder_vtables_dispatch_ = 1 FreeformBuilder_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'AddNodes' , 'SegmentType' , 'EditingType' , 'X1' , 'Y1' , 
			'X2' , 'Y2' , 'X3' , 'Y3' , ), 10, (10, (), [ 
			(3, 1, None, None) , (3, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , (4, 49, '0.0', None) , 
			(4, 49, '0.0', None) , (4, 49, '0.0', None) , (4, 49, '0.0', None) , ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'ConvertToShape' , 'Freeform' , ), 11, (11, (), [ (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
] GroupShapes_vtables_dispatch_ = 1 GroupShapes_vtables_ = [
	(( 'Parent' , 'ppidisp' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pnShapes' , ), 2, (2, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'Item' , ), 0, (0, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'ppienum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 1024 , )),
] HTMLProject_vtables_dispatch_ = 1 HTMLProject_vtables_ = [
	(( 'State' , 'State' , ), 0, (0, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'RefreshProject' , 'Refresh' , ), 1, (1, (), [ (11, 49, 'True', None) , ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'RefreshDocument' , 'Refresh' , ), 2, (2, (), [ (11, 49, 'True', None) , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'HTMLProjectItems' , 'HTMLProjectItems' , ), 3, (3, (), [ (16393, 10, None, "IID('{000C0357-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'ppidisp' , ), 4, (4, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Open' , 'OpenKind' , ), 5, (5, (), [ (3, 49, '0', None) , ], 1 , 1 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
] HTMLProjectItem_vtables_dispatch_ = 1 HTMLProjectItem_vtables_ = [
	(( 'Name' , 'RetValue' , ), 0, (0, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'IsOpen' , 'RetValue' , ), 4, (4, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'LoadFromFile' , 'FileName' , ), 5, (5, (), [ (8, 1, None, None) , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Open' , 'OpenKind' , ), 6, (6, (), [ (3, 49, '0', None) , ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'SaveCopyAs' , 'FileName' , ), 7, (7, (), [ (8, 1, None, None) , ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'Text' , ), 8, (8, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'Text' , ), 8, (8, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'Parent' , 'ppidisp' , ), 10, (10, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
] HTMLProjectItems_vtables_dispatch_ = 1 HTMLProjectItems_vtables_ = [
	(( 'Item' , 'Index' , 'RetValue' , ), 0, (0, (), [ (16396, 1, None, None) , 
			(16393, 10, None, "IID('{000C0358-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'RetValue' , ), 1, (1, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'RetValue' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 1025 , )),
	(( 'Parent' , 'ppidisp' , ), 2, (2, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
] IAccessible_vtables_dispatch_ = 1 IAccessible_vtables_ = [
	(( 'accParent' , 'ppdispParent' , ), -5000, (-5000, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 28 , (3, 0, None, None) , 1088 , )),
	(( 'accChildCount' , 'pcountChildren' , ), -5001, (-5001, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 32 , (3, 0, None, None) , 1088 , )),
	(( 'accChild' , 'varChild' , 'ppdispChild' , ), -5002, (-5002, (), [ (12, 1, None, None) , 
			(16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 1088 , )),
	(( 'accName' , 'varChild' , 'pszName' , ), -5003, (-5003, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 40 , (3, 0, None, None) , 1088 , )),
	(( 'accName' , 'varChild' , 'pszName' , ), -5003, (-5003, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 40 , (3, 0, None, None) , 1088 , )),
	(( 'accValue' , 'varChild' , 'pszValue' , ), -5004, (-5004, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 44 , (3, 0, None, None) , 1088 , )),
	(( 'accValue' , 'varChild' , 'pszValue' , ), -5004, (-5004, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 44 , (3, 0, None, None) , 1088 , )),
	(( 'accDescription' , 'varChild' , 'pszDescription' , ), -5005, (-5005, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 48 , (3, 0, None, None) , 1088 , )),
	(( 'accDescription' , 'varChild' , 'pszDescription' , ), -5005, (-5005, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 48 , (3, 0, None, None) , 1088 , )),
	(( 'accRole' , 'varChild' , 'pvarRole' , ), -5006, (-5006, (), [ (12, 17, None, None) , 
			(16396, 10, None, None) , ], 1 , 2 , 4 , 1 , 52 , (3, 0, None, None) , 1088 , )),
	(( 'accRole' , 'varChild' , 'pvarRole' , ), -5006, (-5006, (), [ (12, 17, None, None) , 
			(16396, 10, None, None) , ], 1 , 2 , 4 , 1 , 52 , (3, 0, None, None) , 1088 , )),
	(( 'accState' , 'varChild' , 'pvarState' , ), -5007, (-5007, (), [ (12, 17, None, None) , 
			(16396, 10, None, None) , ], 1 , 2 , 4 , 1 , 56 , (3, 0, None, None) , 1088 , )),
	(( 'accState' , 'varChild' , 'pvarState' , ), -5007, (-5007, (), [ (12, 17, None, None) , 
			(16396, 10, None, None) , ], 1 , 2 , 4 , 1 , 56 , (3, 0, None, None) , 1088 , )),
	(( 'accHelp' , 'varChild' , 'pszHelp' , ), -5008, (-5008, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 60 , (3, 0, None, None) , 1088 , )),
	(( 'accHelp' , 'varChild' , 'pszHelp' , ), -5008, (-5008, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 60 , (3, 0, None, None) , 1088 , )),
	(( 'accHelpTopic' , 'pszHelpFile' , 'varChild' , 'pidTopic' , ), -5009, (-5009, (), [ 
			(16392, 2, None, None) , (12, 17, None, None) , (16387, 10, None, None) , ], 1 , 2 , 4 , 1 , 64 , (3, 0, None, None) , 1088 , )),
	(( 'accHelpTopic' , 'pszHelpFile' , 'varChild' , 'pidTopic' , ), -5009, (-5009, (), [ 
			(16392, 2, None, None) , (12, 17, None, None) , (16387, 10, None, None) , ], 1 , 2 , 4 , 1 , 64 , (3, 0, None, None) , 1088 , )),
	(( 'accKeyboardShortcut' , 'varChild' , 'pszKeyboardShortcut' , ), -5010, (-5010, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 68 , (3, 0, None, None) , 1088 , )),
	(( 'accKeyboardShortcut' , 'varChild' , 'pszKeyboardShortcut' , ), -5010, (-5010, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 68 , (3, 0, None, None) , 1088 , )),
	(( 'accFocus' , 'pvarChild' , ), -5011, (-5011, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 1088 , )),
	(( 'accSelection' , 'pvarChildren' , ), -5012, (-5012, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 76 , (3, 0, None, None) , 1088 , )),
	(( 'accDefaultAction' , 'varChild' , 'pszDefaultAction' , ), -5013, (-5013, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 80 , (3, 0, None, None) , 1088 , )),
	(( 'accDefaultAction' , 'varChild' , 'pszDefaultAction' , ), -5013, (-5013, (), [ (12, 17, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 1 , 80 , (3, 0, None, None) , 1088 , )),
	(( 'accSelect' , 'flagsSelect' , 'varChild' , ), -5014, (-5014, (), [ (3, 1, None, None) , 
			(12, 17, None, None) , ], 1 , 1 , 4 , 1 , 84 , (3, 0, None, None) , 1088 , )),
	(( 'accLocation' , 'pxLeft' , 'pyTop' , 'pcxWidth' , 'pcyHeight' , 
			'varChild' , ), -5015, (-5015, (), [ (16387, 2, None, None) , (16387, 2, None, None) , (16387, 2, None, None) , 
			(16387, 2, None, None) , (12, 17, None, None) , ], 1 , 1 , 4 , 1 , 88 , (3, 0, None, None) , 1088 , )),
	(( 'accNavigate' , 'navDir' , 'varStart' , 'pvarEndUpAt' , ), -5016, (-5016, (), [ 
			(3, 1, None, None) , (12, 17, None, None) , (16396, 10, None, None) , ], 1 , 1 , 4 , 1 , 92 , (3, 0, None, None) , 1088 , )),
	(( 'accHitTest' , 'xLeft' , 'yTop' , 'pvarChild' , ), -5017, (-5017, (), [ 
			(3, 1, None, None) , (3, 1, None, None) , (16396, 10, None, None) , ], 1 , 1 , 4 , 0 , 96 , (3, 0, None, None) , 1088 , )),
	(( 'accDoDefaultAction' , 'varChild' , ), -5018, (-5018, (), [ (12, 17, None, None) , ], 1 , 1 , 4 , 1 , 100 , (3, 0, None, None) , 1088 , )),
	(( 'accName' , 'varChild' , 'pszName' , ), -5003, (-5003, (), [ (12, 17, None, None) , 
			(8, 1, None, None) , ], 1 , 4 , 4 , 1 , 104 , (3, 0, None, None) , 1088 , )),
	(( 'accName' , 'varChild' , 'pszName' , ), -5003, (-5003, (), [ (12, 17, None, None) , 
			(8, 1, None, None) , ], 1 , 4 , 4 , 1 , 104 , (3, 0, None, None) , 1088 , )),
	(( 'accValue' , 'varChild' , 'pszValue' , ), -5004, (-5004, (), [ (12, 17, None, None) , 
			(8, 1, None, None) , ], 1 , 4 , 4 , 1 , 108 , (3, 0, None, None) , 1088 , )),
	(( 'accValue' , 'varChild' , 'pszValue' , ), -5004, (-5004, (), [ (12, 17, None, None) , 
			(8, 1, None, None) , ], 1 , 4 , 4 , 1 , 108 , (3, 0, None, None) , 1088 , )),
] ICommandBarButtonEvents_vtables_dispatch_ = 1 ICommandBarButtonEvents_vtables_ = [
	(( 'Click' , 'Ctrl' , 'CancelDefault' , ), 1, (1, (), [ (13, 1, None, "IID('{55F88891-7708-11D1-ACEB-006008961DA5}')") , 
			(16395, 3, None, None) , ], 1 , 1 , 4 , 0 , 28 , (24, 0, None, None) , 0 , )),
] ICommandBarComboBoxEvents_vtables_dispatch_ = 1 ICommandBarComboBoxEvents_vtables_ = [
	(( 'Change' , 'Ctrl' , ), 1, (1, (), [ (13, 1, None, "IID('{55F88897-7708-11D1-ACEB-006008961DA5}')") , ], 1 , 1 , 4 , 0 , 28 , (24, 0, None, None) , 0 , )),
] ICommandBarsEvents_vtables_dispatch_ = 1 ICommandBarsEvents_vtables_ = [
	(( 'OnUpdate' , ), 1, (1, (), [ ], 1 , 1 , 4 , 0 , 28 , (24, 0, None, None) , 0 , )),
] IFind_vtables_dispatch_ = 1 IFind_vtables_ = [
	(( 'SearchPath' , 'pbstr' , ), 0, (0, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 28 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'pbstr' , ), 1610743809, (1610743809, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 32 , (3, 0, None, None) , 0 , )),
	(( 'SubDir' , 'retval' , ), 1610743810, (1610743810, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Title' , 'pbstr' , ), 1610743811, (1610743811, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Author' , 'pbstr' , ), 1610743812, (1610743812, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Keywords' , 'pbstr' , ), 1610743813, (1610743813, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Subject' , 'pbstr' , ), 1610743814, (1610743814, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Options' , 'penmOptions' , ), 1610743815, (1610743815, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'MatchCase' , 'retval' , ), 1610743816, (1610743816, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstr' , ), 1610743817, (1610743817, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'PatternMatch' , 'retval' , ), 1610743818, (1610743818, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'DateSavedFrom' , 'pdatSavedFrom' , ), 1610743819, (1610743819, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'DateSavedTo' , 'pdatSavedTo' , ), 1610743820, (1610743820, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'SavedBy' , 'pbstr' , ), 1610743821, (1610743821, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'DateCreatedFrom' , 'pdatCreatedFrom' , ), 1610743822, (1610743822, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'DateCreatedTo' , 'pdatCreatedTo' , ), 1610743823, (1610743823, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'View' , 'penmView' , ), 1610743824, (1610743824, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'SortBy' , 'penmSortBy' , ), 1610743825, (1610743825, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'ListBy' , 'penmListBy' , ), 1610743826, (1610743826, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'SelectedFile' , 'pintSelectedFile' , ), 1610743827, (1610743827, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'Results' , 'pdisp' , ), 1610743828, (1610743828, (), [ (16393, 10, None, "IID('{000C0338-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'Show' , 'pRows' , ), 1610743829, (1610743829, (), [ (16387, 10, None, None) , ], 1 , 1 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'SearchPath' , 'pbstr' , ), 0, (0, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'pbstr' , ), 1610743809, (1610743809, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'SubDir' , 'retval' , ), 1610743810, (1610743810, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'Title' , 'pbstr' , ), 1610743811, (1610743811, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'Author' , 'pbstr' , ), 1610743812, (1610743812, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'Keywords' , 'pbstr' , ), 1610743813, (1610743813, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
	(( 'Subject' , 'pbstr' , ), 1610743814, (1610743814, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 140 , (3, 0, None, None) , 0 , )),
	(( 'Options' , 'penmOptions' , ), 1610743815, (1610743815, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 144 , (3, 0, None, None) , 0 , )),
	(( 'MatchCase' , 'retval' , ), 1610743816, (1610743816, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 148 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstr' , ), 1610743817, (1610743817, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 152 , (3, 0, None, None) , 0 , )),
	(( 'PatternMatch' , 'retval' , ), 1610743818, (1610743818, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 156 , (3, 0, None, None) , 0 , )),
	(( 'DateSavedFrom' , 'pdatSavedFrom' , ), 1610743819, (1610743819, (), [ (12, 1, None, None) , ], 1 , 4 , 4 , 0 , 160 , (3, 0, None, None) , 0 , )),
	(( 'DateSavedTo' , 'pdatSavedTo' , ), 1610743820, (1610743820, (), [ (12, 1, None, None) , ], 1 , 4 , 4 , 0 , 164 , (3, 0, None, None) , 0 , )),
	(( 'SavedBy' , 'pbstr' , ), 1610743821, (1610743821, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 168 , (3, 0, None, None) , 0 , )),
	(( 'DateCreatedFrom' , 'pdatCreatedFrom' , ), 1610743822, (1610743822, (), [ (12, 1, None, None) , ], 1 , 4 , 4 , 0 , 172 , (3, 0, None, None) , 0 , )),
	(( 'DateCreatedTo' , 'pdatCreatedTo' , ), 1610743823, (1610743823, (), [ (12, 1, None, None) , ], 1 , 4 , 4 , 0 , 176 , (3, 0, None, None) , 0 , )),
	(( 'View' , 'penmView' , ), 1610743824, (1610743824, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 180 , (3, 0, None, None) , 0 , )),
	(( 'SortBy' , 'penmSortBy' , ), 1610743825, (1610743825, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 184 , (3, 0, None, None) , 0 , )),
	(( 'ListBy' , 'penmListBy' , ), 1610743826, (1610743826, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 188 , (3, 0, None, None) , 0 , )),
	(( 'SelectedFile' , 'pintSelectedFile' , ), 1610743827, (1610743827, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 192 , (3, 0, None, None) , 0 , )),
	(( 'Execute' , ), 1610743850, (1610743850, (), [ ], 1 , 1 , 4 , 0 , 196 , (3, 0, None, None) , 0 , )),
	(( 'Load' , 'bstrQueryName' , ), 1610743851, (1610743851, (), [ (8, 1, None, None) , ], 1 , 1 , 4 , 0 , 200 , (3, 0, None, None) , 0 , )),
	(( 'Save' , 'bstrQueryName' , ), 1610743852, (1610743852, (), [ (8, 1, None, None) , ], 1 , 1 , 4 , 0 , 204 , (3, 0, None, None) , 0 , )),
	(( 'Delete' , 'bstrQueryName' , ), 1610743853, (1610743853, (), [ (8, 1, None, None) , ], 1 , 1 , 4 , 0 , 208 , (3, 0, None, None) , 0 , )),
	(( 'FileType' , 'plFileType' , ), 1610743854, (1610743854, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 212 , (3, 0, None, None) , 0 , )),
	(( 'FileType' , 'plFileType' , ), 1610743854, (1610743854, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 216 , (3, 0, None, None) , 0 , )),
] IFoundFiles_vtables_dispatch_ = 1 IFoundFiles_vtables_ = [
	(( 'Item' , 'Index' , 'pbstr' , ), 0, (0, (), [ (3, 1, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 28 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pCount' , ), 1610743809, (1610743809, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 32 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'ppunkEnum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 1025 , )),
] IMsoDispCagNotifySink_vtables_dispatch_ = 1 IMsoDispCagNotifySink_vtables_ = [
	(( 'InsertClip' , 'pClipMoniker' , 'pItemMoniker' , ), 1, (1, (), [ (13, 1, None, None) , 
			(13, 1, None, None) , ], 1 , 1 , 4 , 0 , 28 , (3, 0, None, None) , 0 , )),
	(( 'WindowIsClosing' , ), 2, (2, (), [ ], 1 , 1 , 4 , 0 , 32 , (3, 0, None, None) , 0 , )),
] LanguageSettings_vtables_dispatch_ = 1 LanguageSettings_vtables_ = [
	(( 'LanguageID' , 'Id' , 'plid' , ), 1, (1, (), [ (3, 1, None, None) , 
			(16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'LanguagePreferredForEditing' , 'lid' , 'pf' , ), 2, (2, (), [ (3, 1, None, None) , 
			(16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
] LineFormat_vtables_dispatch_ = 1 LineFormat_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'BackColor' , 'BackColor' , ), 100, (100, (), [ (16393, 10, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'BackColor' , 'BackColor' , ), 100, (100, (), [ (9, 1, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 4 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'BeginArrowheadLength' , 'BeginArrowheadLength' , ), 101, (101, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'BeginArrowheadLength' , 'BeginArrowheadLength' , ), 101, (101, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'BeginArrowheadStyle' , 'BeginArrowheadStyle' , ), 102, (102, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'BeginArrowheadStyle' , 'BeginArrowheadStyle' , ), 102, (102, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'BeginArrowheadWidth' , 'BeginArrowheadWidth' , ), 103, (103, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'BeginArrowheadWidth' , 'BeginArrowheadWidth' , ), 103, (103, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'DashStyle' , 'DashStyle' , ), 104, (104, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'DashStyle' , 'DashStyle' , ), 104, (104, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'EndArrowheadLength' , 'EndArrowheadLength' , ), 105, (105, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'EndArrowheadLength' , 'EndArrowheadLength' , ), 105, (105, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'EndArrowheadStyle' , 'EndArrowheadStyle' , ), 106, (106, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'EndArrowheadStyle' , 'EndArrowheadStyle' , ), 106, (106, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'EndArrowheadWidth' , 'EndArrowheadWidth' , ), 107, (107, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'EndArrowheadWidth' , 'EndArrowheadWidth' , ), 107, (107, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'ForeColor' , 'ForeColor' , ), 108, (108, (), [ (16393, 10, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'ForeColor' , 'ForeColor' , ), 108, (108, (), [ (9, 1, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 4 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'Pattern' , 'Pattern' , ), 109, (109, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'Pattern' , 'Pattern' , ), 109, (109, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'Style' , 'Style' , ), 110, (110, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'Style' , 'Style' , ), 110, (110, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'Transparency' , 'Transparency' , ), 111, (111, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'Transparency' , 'Transparency' , ), 111, (111, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 112, (112, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 112, (112, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 140 , (3, 0, None, None) , 0 , )),
	(( 'Weight' , 'Weight' , ), 113, (113, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 144 , (3, 0, None, None) , 0 , )),
	(( 'Weight' , 'Weight' , ), 113, (113, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 148 , (3, 0, None, None) , 0 , )),
] MsoDebugOptions_vtables_dispatch_ = 1 MsoDebugOptions_vtables_ = [
	(( 'FeatureReports' , 'puintFeatureReports' , ), 4, (4, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 64 , )),
	(( 'FeatureReports' , 'puintFeatureReports' , ), 4, (4, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 40 , (3, 0, None, None) , 64 , )),
] PictureFormat_vtables_dispatch_ = 1 PictureFormat_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'IncrementBrightness' , 'Increment' , ), 10, (10, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'IncrementContrast' , 'Increment' , ), 11, (11, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Brightness' , 'Brightness' , ), 100, (100, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Brightness' , 'Brightness' , ), 100, (100, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'ColorType' , 'ColorType' , ), 101, (101, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'ColorType' , 'ColorType' , ), 101, (101, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'Contrast' , 'Contrast' , ), 102, (102, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'Contrast' , 'Contrast' , ), 102, (102, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'CropBottom' , 'CropBottom' , ), 103, (103, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'CropBottom' , 'CropBottom' , ), 103, (103, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'CropLeft' , 'CropLeft' , ), 104, (104, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'CropLeft' , 'CropLeft' , ), 104, (104, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'CropRight' , 'CropRight' , ), 105, (105, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'CropRight' , 'CropRight' , ), 105, (105, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'CropTop' , 'CropTop' , ), 106, (106, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'CropTop' , 'CropTop' , ), 106, (106, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'TransparencyColor' , 'TransparencyColor' , ), 107, (107, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'TransparencyColor' , 'TransparencyColor' , ), 107, (107, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'TransparentBackground' , 'TransparentBackground' , ), 108, (108, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'TransparentBackground' , 'TransparentBackground' , ), 108, (108, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
] PropertyTest_vtables_dispatch_ = 1 PropertyTest_vtables_ = [
	(( 'Name' , 'pbstrRetVal' , ), 0, (0, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Condition' , 'pConditionRetVal' , ), 2, (2, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Value' , 'pvargRetVal' , ), 3, (3, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'SecondValue' , 'pvargRetVal2' , ), 4, (4, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Connector' , 'pConnector' , ), 5, (5, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
] PropertyTests_vtables_dispatch_ = 1 PropertyTests_vtables_ = [
	(( 'Item' , 'Index' , 'lcid' , 'ppIDocProp' , ), 0, (0, (), [ 
			(3, 1, None, None) , (3, 5, None, None) , (16393, 10, None, "IID('{000C0333-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pc' , ), 4, (4, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Add' , 'Name' , 'Condition' , 'Value' , 'SecondValue' , 
			'Connector' , ), 5, (5, (), [ (8, 1, None, None) , (3, 1, None, None) , (12, 17, None, None) , 
			(12, 17, None, None) , (3, 49, '1', None) , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Remove' , 'Index' , ), 6, (6, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'ppunkEnum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 1024 , )),
] Script_vtables_dispatch_ = 1 Script_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1610809344, (1610809344, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Extended' , 'Extended' , ), 1610809345, (1610809345, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Extended' , 'Extended' , ), 1610809345, (1610809345, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Id' , 'Id' , ), 1610809347, (1610809347, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Id' , 'Id' , ), 1610809347, (1610809347, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Language' , 'Language' , ), 1610809349, (1610809349, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Language' , 'Language' , ), 1610809349, (1610809349, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'Location' , 'Location' , ), 1610809351, (1610809351, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'Delete' , ), 1610809352, (1610809352, (), [ ], 1 , 1 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'Shape' , 'Object' , ), 1610809353, (1610809353, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'ScriptText' , 'Script' , ), 0, (0, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'ScriptText' , 'Script' , ), 0, (0, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
] Scripts_vtables_dispatch_ = 1 Scripts_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1610809344, (1610809344, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'Count' , ), 1610809345, (1610809345, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , '_NewEnum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 1024 , )),
	(( 'Item' , 'Index' , 'Item' , ), 0, (0, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C0341-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Add' , 'Anchor' , 'Location' , 'Language' , 'Id' , 
			'Extended' , 'ScriptText' , 'Add' , ), 1610809348, (1610809348, (), [ (9, 49, 'None', None) , 
			(3, 49, '2', None) , (3, 49, '2', None) , (8, 49, "''", None) , (8, 49, "''", None) , (8, 49, "''", None) , 
			(16393, 10, None, "IID('{000C0341-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 52 , (3, 32, None, None) , 0 , )),
	(( 'Delete' , ), 1610809349, (1610809349, (), [ ], 1 , 1 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
] ShadowFormat_vtables_dispatch_ = 1 ShadowFormat_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'IncrementOffsetX' , 'Increment' , ), 10, (10, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'IncrementOffsetY' , 'Increment' , ), 11, (11, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'ForeColor' , 'ForeColor' , ), 100, (100, (), [ (16393, 10, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'ForeColor' , 'ForeColor' , ), 100, (100, (), [ (9, 1, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 4 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Obscured' , 'Obscured' , ), 101, (101, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Obscured' , 'Obscured' , ), 101, (101, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'OffsetX' , 'OffsetX' , ), 102, (102, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'OffsetX' , 'OffsetX' , ), 102, (102, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'OffsetY' , 'OffsetY' , ), 103, (103, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'OffsetY' , 'OffsetY' , ), 103, (103, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'Transparency' , 'Transparency' , ), 104, (104, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'Transparency' , 'Transparency' , ), 104, (104, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 105, (105, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 105, (105, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 106, (106, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 106, (106, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
] Shape_vtables_dispatch_ = 1 Shape_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Apply' , ), 10, (10, (), [ ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Delete' , ), 11, (11, (), [ ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Duplicate' , 'Duplicate' , ), 12, (12, (), [ (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'Flip' , 'FlipCmd' , ), 13, (13, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'IncrementLeft' , 'Increment' , ), 14, (14, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'IncrementRotation' , 'Increment' , ), 15, (15, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'IncrementTop' , 'Increment' , ), 16, (16, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'PickUp' , ), 17, (17, (), [ ], 1 , 1 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'RerouteConnections' , ), 18, (18, (), [ ], 1 , 1 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'ScaleHeight' , 'Factor' , 'RelativeToOriginalSize' , 'fScale' , ), 19, (19, (), [ 
			(4, 1, None, None) , (3, 1, None, None) , (3, 49, '0', None) , ], 1 , 1 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'ScaleWidth' , 'Factor' , 'RelativeToOriginalSize' , 'fScale' , ), 20, (20, (), [ 
			(4, 1, None, None) , (3, 1, None, None) , (3, 49, '0', None) , ], 1 , 1 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'Select' , 'Replace' , ), 21, (21, (), [ (12, 17, None, None) , ], 1 , 1 , 4 , 1 , 84 , (3, 0, None, None) , 0 , )),
	(( 'SetShapesDefaultProperties' , ), 22, (22, (), [ ], 1 , 1 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'Ungroup' , 'Ungroup' , ), 23, (23, (), [ (16393, 10, None, "IID('{000C031D-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'ZOrder' , 'ZOrderCmd' , ), 24, (24, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'Adjustments' , 'Adjustments' , ), 100, (100, (), [ (16393, 10, None, "IID('{000C0310-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'AutoShapeType' , 'AutoShapeType' , ), 101, (101, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'AutoShapeType' , 'AutoShapeType' , ), 101, (101, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'BlackWhiteMode' , 'BlackWhiteMode' , ), 102, (102, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'BlackWhiteMode' , 'BlackWhiteMode' , ), 102, (102, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'Callout' , 'Callout' , ), 103, (103, (), [ (16393, 10, None, "IID('{000C0311-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'ConnectionSiteCount' , 'ConnectionSiteCount' , ), 104, (104, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'Connector' , 'Connector' , ), 105, (105, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'ConnectorFormat' , 'ConnectorFormat' , ), 106, (106, (), [ (16393, 10, None, "IID('{000C0313-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'Fill' , 'Fill' , ), 107, (107, (), [ (16393, 10, None, "IID('{000C0314-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
	(( 'GroupItems' , 'GroupItems' , ), 108, (108, (), [ (16393, 10, None, "IID('{000C0316-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 140 , (3, 0, None, None) , 0 , )),
	(( 'Height' , 'Height' , ), 109, (109, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 144 , (3, 0, None, None) , 0 , )),
	(( 'Height' , 'Height' , ), 109, (109, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 148 , (3, 0, None, None) , 0 , )),
	(( 'HorizontalFlip' , 'HorizontalFlip' , ), 110, (110, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 152 , (3, 0, None, None) , 0 , )),
	(( 'Left' , 'Left' , ), 111, (111, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 156 , (3, 0, None, None) , 0 , )),
	(( 'Left' , 'Left' , ), 111, (111, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 160 , (3, 0, None, None) , 0 , )),
	(( 'Line' , 'Line' , ), 112, (112, (), [ (16393, 10, None, "IID('{000C0317-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 164 , (3, 0, None, None) , 0 , )),
	(( 'LockAspectRatio' , 'LockAspectRatio' , ), 113, (113, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 168 , (3, 0, None, None) , 0 , )),
	(( 'LockAspectRatio' , 'LockAspectRatio' , ), 113, (113, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 172 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'Name' , ), 115, (115, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 176 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'Name' , ), 115, (115, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 180 , (3, 0, None, None) , 0 , )),
	(( 'Nodes' , 'Nodes' , ), 116, (116, (), [ (16393, 10, None, "IID('{000C0319-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 184 , (3, 0, None, None) , 0 , )),
	(( 'Rotation' , 'Rotation' , ), 117, (117, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 188 , (3, 0, None, None) , 0 , )),
	(( 'Rotation' , 'Rotation' , ), 117, (117, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 192 , (3, 0, None, None) , 0 , )),
	(( 'PictureFormat' , 'Picture' , ), 118, (118, (), [ (16393, 10, None, "IID('{000C031A-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 196 , (3, 0, None, None) , 0 , )),
	(( 'Shadow' , 'Shadow' , ), 119, (119, (), [ (16393, 10, None, "IID('{000C031B-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 200 , (3, 0, None, None) , 0 , )),
	(( 'TextEffect' , 'TextEffect' , ), 120, (120, (), [ (16393, 10, None, "IID('{000C031F-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 204 , (3, 0, None, None) , 0 , )),
	(( 'TextFrame' , 'TextFrame' , ), 121, (121, (), [ (16393, 10, None, "IID('{000C0320-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 208 , (3, 0, None, None) , 0 , )),
	(( 'ThreeD' , 'ThreeD' , ), 122, (122, (), [ (16393, 10, None, "IID('{000C0321-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 212 , (3, 0, None, None) , 0 , )),
	(( 'Top' , 'Top' , ), 123, (123, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 216 , (3, 0, None, None) , 0 , )),
	(( 'Top' , 'Top' , ), 123, (123, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 220 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 124, (124, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 224 , (3, 0, None, None) , 0 , )),
	(( 'VerticalFlip' , 'VerticalFlip' , ), 125, (125, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 228 , (3, 0, None, None) , 0 , )),
	(( 'Vertices' , 'Vertices' , ), 126, (126, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 232 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 127, (127, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 236 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 127, (127, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 240 , (3, 0, None, None) , 0 , )),
	(( 'Width' , 'Width' , ), 128, (128, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 244 , (3, 0, None, None) , 0 , )),
	(( 'Width' , 'Width' , ), 128, (128, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 248 , (3, 0, None, None) , 0 , )),
	(( 'ZOrderPosition' , 'ZOrderPosition' , ), 129, (129, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 252 , (3, 0, None, None) , 0 , )),
	(( 'Script' , 'Script' , ), 130, (130, (), [ (16393, 10, None, "IID('{000C0341-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 256 , (3, 0, None, None) , 0 , )),
	(( 'AlternativeText' , 'AlternativeText' , ), 131, (131, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 260 , (3, 0, None, None) , 0 , )),
	(( 'AlternativeText' , 'AlternativeText' , ), 131, (131, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 264 , (3, 0, None, None) , 0 , )),
] ShapeNode_vtables_dispatch_ = 1 ShapeNode_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'EditingType' , 'EditingType' , ), 100, (100, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Points' , 'Points' , ), 101, (101, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'SegmentType' , 'SegmentType' , ), 102, (102, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
] ShapeNodes_vtables_dispatch_ = 1 ShapeNodes_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'Count' , ), 2, (2, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'Item' , ), 0, (0, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C0318-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , '_NewEnum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 1024 , )),
	(( 'Delete' , 'Index' , ), 11, (11, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Insert' , 'Index' , 'SegmentType' , 'EditingType' , 'X1' , 
			'Y1' , 'X2' , 'Y2' , 'X3' , 'Y3' , 
			), 12, (12, (), [ (3, 1, None, None) , (3, 1, None, None) , (3, 1, None, None) , (4, 1, None, None) , 
			(4, 1, None, None) , (4, 49, '0.0', None) , (4, 49, '0.0', None) , (4, 49, '0.0', None) , (4, 49, '0.0', None) , ], 1 , 1 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'SetEditingType' , 'Index' , 'EditingType' , ), 13, (13, (), [ (3, 1, None, None) , 
			(3, 1, None, None) , ], 1 , 1 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'SetPosition' , 'Index' , 'X1' , 'Y1' , ), 14, (14, (), [ 
			(3, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'SetSegmentType' , 'Index' , 'SegmentType' , ), 15, (15, (), [ (3, 1, None, None) , 
			(3, 1, None, None) , ], 1 , 1 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
] ShapeRange_vtables_dispatch_ = 1 ShapeRange_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'Count' , ), 2, (2, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'Item' , ), 0, (0, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , '_NewEnum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 1024 , )),
	(( 'Align' , 'AlignCmd' , 'RelativeTo' , ), 10, (10, (), [ (3, 1, None, None) , 
			(3, 1, None, None) , ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'Apply' , ), 11, (11, (), [ ], 1 , 1 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Delete' , ), 12, (12, (), [ ], 1 , 1 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'Distribute' , 'DistributeCmd' , 'RelativeTo' , ), 13, (13, (), [ (3, 1, None, None) , 
			(3, 1, None, None) , ], 1 , 1 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'Duplicate' , 'Duplicate' , ), 14, (14, (), [ (16393, 10, None, "IID('{000C031D-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'Flip' , 'FlipCmd' , ), 15, (15, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'IncrementLeft' , 'Increment' , ), 16, (16, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'IncrementRotation' , 'Increment' , ), 17, (17, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'IncrementTop' , 'Increment' , ), 18, (18, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'Group' , 'Group' , ), 19, (19, (), [ (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'PickUp' , ), 20, (20, (), [ ], 1 , 1 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'Regroup' , 'Regroup' , ), 21, (21, (), [ (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'RerouteConnections' , ), 22, (22, (), [ ], 1 , 1 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'ScaleHeight' , 'Factor' , 'RelativeToOriginalSize' , 'fScale' , ), 23, (23, (), [ 
			(4, 1, None, None) , (3, 1, None, None) , (3, 49, '0', None) , ], 1 , 1 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'ScaleWidth' , 'Factor' , 'RelativeToOriginalSize' , 'fScale' , ), 24, (24, (), [ 
			(4, 1, None, None) , (3, 1, None, None) , (3, 49, '0', None) , ], 1 , 1 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'Select' , 'Replace' , ), 25, (25, (), [ (12, 17, None, None) , ], 1 , 1 , 4 , 1 , 112 , (3, 0, None, None) , 0 , )),
	(( 'SetShapesDefaultProperties' , ), 26, (26, (), [ ], 1 , 1 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'Ungroup' , 'Ungroup' , ), 27, (27, (), [ (16393, 10, None, "IID('{000C031D-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'ZOrder' , 'ZOrderCmd' , ), 28, (28, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'Adjustments' , 'Adjustments' , ), 100, (100, (), [ (16393, 10, None, "IID('{000C0310-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'AutoShapeType' , 'AutoShapeType' , ), 101, (101, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'AutoShapeType' , 'AutoShapeType' , ), 101, (101, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
	(( 'BlackWhiteMode' , 'BlackWhiteMode' , ), 102, (102, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 140 , (3, 0, None, None) , 0 , )),
	(( 'BlackWhiteMode' , 'BlackWhiteMode' , ), 102, (102, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 144 , (3, 0, None, None) , 0 , )),
	(( 'Callout' , 'Callout' , ), 103, (103, (), [ (16393, 10, None, "IID('{000C0311-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 148 , (3, 0, None, None) , 0 , )),
	(( 'ConnectionSiteCount' , 'ConnectionSiteCount' , ), 104, (104, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 152 , (3, 0, None, None) , 0 , )),
	(( 'Connector' , 'Connector' , ), 105, (105, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 156 , (3, 0, None, None) , 0 , )),
	(( 'ConnectorFormat' , 'ConnectorFormat' , ), 106, (106, (), [ (16393, 10, None, "IID('{000C0313-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 160 , (3, 0, None, None) , 0 , )),
	(( 'Fill' , 'Fill' , ), 107, (107, (), [ (16393, 10, None, "IID('{000C0314-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 164 , (3, 0, None, None) , 0 , )),
	(( 'GroupItems' , 'GroupItems' , ), 108, (108, (), [ (16393, 10, None, "IID('{000C0316-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 168 , (3, 0, None, None) , 0 , )),
	(( 'Height' , 'Height' , ), 109, (109, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 172 , (3, 0, None, None) , 0 , )),
	(( 'Height' , 'Height' , ), 109, (109, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 176 , (3, 0, None, None) , 0 , )),
	(( 'HorizontalFlip' , 'HorizontalFlip' , ), 110, (110, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 180 , (3, 0, None, None) , 0 , )),
	(( 'Left' , 'Left' , ), 111, (111, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 184 , (3, 0, None, None) , 0 , )),
	(( 'Left' , 'Left' , ), 111, (111, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 188 , (3, 0, None, None) , 0 , )),
	(( 'Line' , 'Line' , ), 112, (112, (), [ (16393, 10, None, "IID('{000C0317-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 192 , (3, 0, None, None) , 0 , )),
	(( 'LockAspectRatio' , 'LockAspectRatio' , ), 113, (113, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 196 , (3, 0, None, None) , 0 , )),
	(( 'LockAspectRatio' , 'LockAspectRatio' , ), 113, (113, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 200 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'Name' , ), 115, (115, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 204 , (3, 0, None, None) , 0 , )),
	(( 'Name' , 'Name' , ), 115, (115, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 208 , (3, 0, None, None) , 0 , )),
	(( 'Nodes' , 'Nodes' , ), 116, (116, (), [ (16393, 10, None, "IID('{000C0319-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 212 , (3, 0, None, None) , 0 , )),
	(( 'Rotation' , 'Rotation' , ), 117, (117, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 216 , (3, 0, None, None) , 0 , )),
	(( 'Rotation' , 'Rotation' , ), 117, (117, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 220 , (3, 0, None, None) , 0 , )),
	(( 'PictureFormat' , 'Picture' , ), 118, (118, (), [ (16393, 10, None, "IID('{000C031A-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 224 , (3, 0, None, None) , 0 , )),
	(( 'Shadow' , 'Shadow' , ), 119, (119, (), [ (16393, 10, None, "IID('{000C031B-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 228 , (3, 0, None, None) , 0 , )),
	(( 'TextEffect' , 'TextEffect' , ), 120, (120, (), [ (16393, 10, None, "IID('{000C031F-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 232 , (3, 0, None, None) , 0 , )),
	(( 'TextFrame' , 'TextFrame' , ), 121, (121, (), [ (16393, 10, None, "IID('{000C0320-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 236 , (3, 0, None, None) , 0 , )),
	(( 'ThreeD' , 'ThreeD' , ), 122, (122, (), [ (16393, 10, None, "IID('{000C0321-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 240 , (3, 0, None, None) , 0 , )),
	(( 'Top' , 'Top' , ), 123, (123, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 244 , (3, 0, None, None) , 0 , )),
	(( 'Top' , 'Top' , ), 123, (123, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 248 , (3, 0, None, None) , 0 , )),
	(( 'Type' , 'Type' , ), 124, (124, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 252 , (3, 0, None, None) , 0 , )),
	(( 'VerticalFlip' , 'VerticalFlip' , ), 125, (125, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 256 , (3, 0, None, None) , 0 , )),
	(( 'Vertices' , 'Vertices' , ), 126, (126, (), [ (16396, 10, None, None) , ], 1 , 2 , 4 , 0 , 260 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 127, (127, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 264 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 127, (127, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 268 , (3, 0, None, None) , 0 , )),
	(( 'Width' , 'Width' , ), 128, (128, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 272 , (3, 0, None, None) , 0 , )),
	(( 'Width' , 'Width' , ), 128, (128, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 276 , (3, 0, None, None) , 0 , )),
	(( 'ZOrderPosition' , 'ZOrderPosition' , ), 129, (129, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 280 , (3, 0, None, None) , 0 , )),
	(( 'Script' , 'Script' , ), 130, (130, (), [ (16393, 10, None, "IID('{000C0341-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 284 , (3, 0, None, None) , 0 , )),
	(( 'AlternativeText' , 'AlternativeText' , ), 131, (131, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 288 , (3, 0, None, None) , 0 , )),
	(( 'AlternativeText' , 'AlternativeText' , ), 131, (131, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 292 , (3, 0, None, None) , 0 , )),
] Shapes_vtables_dispatch_ = 1 Shapes_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'Count' , ), 2, (2, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'Item' , ), 0, (0, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , '_NewEnum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 1024 , )),
	(( 'AddCallout' , 'Type' , 'Left' , 'Top' , 'Width' , 
			'Height' , 'Callout' , ), 10, (10, (), [ (3, 1, None, None) , (4, 1, None, None) , 
			(4, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'AddConnector' , 'Type' , 'BeginX' , 'BeginY' , 'EndX' , 
			'EndY' , 'Connector' , ), 11, (11, (), [ (3, 1, None, None) , (4, 1, None, None) , 
			(4, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'AddCurve' , 'SafeArrayOfPoints' , 'Curve' , ), 12, (12, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'AddLabel' , 'Orientation' , 'Left' , 'Top' , 'Width' , 
			'Height' , 'Label' , ), 13, (13, (), [ (3, 1, None, None) , (4, 1, None, None) , 
			(4, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'AddLine' , 'BeginX' , 'BeginY' , 'EndX' , 'EndY' , 
			'Line' , ), 14, (14, (), [ (4, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , 
			(4, 1, None, None) , (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'AddPicture' , 'FileName' , 'LinkToFile' , 'SaveWithDocument' , 'Left' , 
			'Top' , 'Width' , 'Height' , 'Picture' , ), 15, (15, (), [ 
			(8, 1, None, None) , (3, 1, None, None) , (3, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , 
			(4, 49, '-1.0', None) , (4, 49, '-1.0', None) , (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'AddPolyline' , 'SafeArrayOfPoints' , 'Polyline' , ), 16, (16, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'AddShape' , 'Type' , 'Left' , 'Top' , 'Width' , 
			'Height' , 'Shape' , ), 17, (17, (), [ (3, 1, None, None) , (4, 1, None, None) , 
			(4, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'AddTextEffect' , 'PresetTextEffect' , 'Text' , 'FontName' , 'FontSize' , 
			'FontBold' , 'FontItalic' , 'Left' , 'Top' , 'TextEffect' , 
			), 18, (18, (), [ (3, 1, None, None) , (8, 1, None, None) , (8, 1, None, None) , (4, 1, None, None) , 
			(3, 1, None, None) , (3, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'AddTextbox' , 'Orientation' , 'Left' , 'Top' , 'Width' , 
			'Height' , 'Textbox' , ), 19, (19, (), [ (3, 1, None, None) , (4, 1, None, None) , 
			(4, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'BuildFreeform' , 'EditingType' , 'X1' , 'Y1' , 'FreeformBuilder' , 
			), 20, (20, (), [ (3, 1, None, None) , (4, 1, None, None) , (4, 1, None, None) , (16393, 10, None, "IID('{000C0315-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'Range' , 'Index' , 'Range' , ), 21, (21, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C031D-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'SelectAll' , ), 22, (22, (), [ ], 1 , 1 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'Background' , 'Background' , ), 100, (100, (), [ (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'Default' , 'Default' , ), 101, (101, (), [ (16393, 10, None, "IID('{000C031C-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
] TextEffectFormat_vtables_dispatch_ = 1 TextEffectFormat_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'ToggleVerticalText' , ), 10, (10, (), [ ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Alignment' , 'Alignment' , ), 100, (100, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Alignment' , 'Alignment' , ), 100, (100, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'FontBold' , 'FontBold' , ), 101, (101, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'FontBold' , 'FontBold' , ), 101, (101, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'FontItalic' , 'FontItalic' , ), 102, (102, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'FontItalic' , 'FontItalic' , ), 102, (102, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'FontName' , 'FontName' , ), 103, (103, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'FontName' , 'FontName' , ), 103, (103, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'FontSize' , 'FontSize' , ), 104, (104, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'FontSize' , 'FontSize' , ), 104, (104, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'KernedPairs' , 'KernedPairs' , ), 105, (105, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'KernedPairs' , 'KernedPairs' , ), 105, (105, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'NormalizedHeight' , 'NormalizedHeight' , ), 106, (106, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'NormalizedHeight' , 'NormalizedHeight' , ), 106, (106, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'PresetShape' , 'PresetShape' , ), 107, (107, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'PresetShape' , 'PresetShape' , ), 107, (107, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'PresetTextEffect' , 'Preset' , ), 108, (108, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'PresetTextEffect' , 'Preset' , ), 108, (108, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'RotatedChars' , 'RotatedChars' , ), 109, (109, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'RotatedChars' , 'RotatedChars' , ), 109, (109, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'Text' , ), 110, (110, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'Text' , ), 110, (110, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'Tracking' , 'Tracking' , ), 111, (111, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'Tracking' , 'Tracking' , ), 111, (111, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
] TextFrame_vtables_dispatch_ = 1 TextFrame_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'MarginBottom' , 'MarginBottom' , ), 100, (100, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'MarginBottom' , 'MarginBottom' , ), 100, (100, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'MarginLeft' , 'MarginLeft' , ), 101, (101, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'MarginLeft' , 'MarginLeft' , ), 101, (101, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'MarginRight' , 'MarginRight' , ), 102, (102, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'MarginRight' , 'MarginRight' , ), 102, (102, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'MarginTop' , 'MarginTop' , ), 103, (103, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'MarginTop' , 'MarginTop' , ), 103, (103, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'Orientation' , 'Orientation' , ), 104, (104, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'Orientation' , 'Orientation' , ), 104, (104, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
] ThreeDFormat_vtables_dispatch_ = 1 ThreeDFormat_vtables_ = [
	(( 'Parent' , 'Parent' , ), 1, (1, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'IncrementRotationX' , 'Increment' , ), 10, (10, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'IncrementRotationY' , 'Increment' , ), 11, (11, (), [ (4, 1, None, None) , ], 1 , 1 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'ResetRotation' , ), 12, (12, (), [ ], 1 , 1 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'SetThreeDFormat' , 'PresetThreeDFormat' , ), 13, (13, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'SetExtrusionDirection' , 'PresetExtrusionDirection' , ), 14, (14, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'Depth' , 'Depth' , ), 100, (100, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'Depth' , 'Depth' , ), 100, (100, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'ExtrusionColor' , 'ExtrusionColor' , ), 101, (101, (), [ (16393, 10, None, "IID('{000C0312-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 68 , (3, 0, None, None) , 0 , )),
	(( 'ExtrusionColorType' , 'ExtrusionColorType' , ), 102, (102, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'ExtrusionColorType' , 'ExtrusionColorType' , ), 102, (102, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'Perspective' , 'Perspective' , ), 103, (103, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'Perspective' , 'Perspective' , ), 103, (103, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'PresetExtrusionDirection' , 'PresetExtrusionDirection' , ), 104, (104, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( 'PresetLightingDirection' , 'PresetLightingDirection' , ), 105, (105, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 92 , (3, 0, None, None) , 0 , )),
	(( 'PresetLightingDirection' , 'PresetLightingDirection' , ), 105, (105, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'PresetLightingSoftness' , 'PresetLightingSoftness' , ), 106, (106, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'PresetLightingSoftness' , 'PresetLightingSoftness' , ), 106, (106, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 104 , (3, 0, None, None) , 0 , )),
	(( 'PresetMaterial' , 'PresetMaterial' , ), 107, (107, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 108 , (3, 0, None, None) , 0 , )),
	(( 'PresetMaterial' , 'PresetMaterial' , ), 107, (107, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'PresetThreeDFormat' , 'PresetThreeDFormat' , ), 108, (108, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'RotationX' , 'RotationX' , ), 109, (109, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 120 , (3, 0, None, None) , 0 , )),
	(( 'RotationX' , 'RotationX' , ), 109, (109, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 124 , (3, 0, None, None) , 0 , )),
	(( 'RotationY' , 'RotationY' , ), 110, (110, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'RotationY' , 'RotationY' , ), 110, (110, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 111, (111, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 136 , (3, 0, None, None) , 0 , )),
	(( 'Visible' , 'Visible' , ), 111, (111, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 140 , (3, 0, None, None) , 0 , )),
] WebPageFont_vtables_dispatch_ = 1 WebPageFont_vtables_ = [
	(( 'ProportionalFont' , 'pstr' , ), 10, (10, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'ProportionalFont' , 'pstr' , ), 10, (10, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'ProportionalFontSize' , 'pf' , ), 11, (11, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 0 , )),
	(( 'ProportionalFontSize' , 'pf' , ), 11, (11, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'FixedWidthFont' , 'pstr' , ), 12, (12, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'FixedWidthFont' , 'pstr' , ), 12, (12, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'FixedWidthFontSize' , 'pf' , ), 13, (13, (), [ (16388, 10, None, None) , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'FixedWidthFontSize' , 'pf' , ), 13, (13, (), [ (4, 1, None, None) , ], 1 , 4 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
] WebPageFonts_vtables_dispatch_ = 1 WebPageFonts_vtables_ = [
	(( 'Count' , 'Count' , ), 1, (1, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'Item' , ), 0, (0, (), [ (3, 1, None, None) , 
			(16393, 10, None, "IID('{000C0913-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , '_NewEnum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 44 , (3, 0, None, None) , 1024 , )),
] _CommandBarActiveX_vtables_dispatch_ = 1 _CommandBarActiveX_vtables_ = [
	(( 'ControlCLSID' , 'pbstrClsid' , ), 1610940416, (1610940416, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 332 , (3, 0, None, None) , 0 , )),
	(( 'ControlCLSID' , 'pbstrClsid' , ), 1610940416, (1610940416, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 336 , (3, 0, None, None) , 0 , )),
	(( 'QueryControlInterface' , 'bstrIid' , 'ppUnk' , ), 1610940418, (1610940418, (), [ (8, 1, None, None) , 
			(16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 340 , (3, 0, None, None) , 0 , )),
	(( 'SetInnerObjectFactory' , 'pUnk' , ), 1610940419, (1610940419, (), [ (13, 1, None, None) , ], 1 , 1 , 4 , 0 , 344 , (3, 0, None, None) , 0 , )),
	(( 'EnsureControl' , ), 1610940420, (1610940420, (), [ ], 1 , 1 , 4 , 0 , 348 , (3, 0, None, None) , 0 , )),
	(( 'InitWith' , ), 1610940421, (1610940421, (), [ (13, 1, None, None) , ], 1 , 4 , 4 , 0 , 352 , (3, 0, None, None) , 0 , )),
] _CommandBarButton_vtables_dispatch_ = 1 _CommandBarButton_vtables_ = [
	(( 'BuiltInFace' , 'pvarfBuiltIn' , ), 1610940416, (1610940416, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 332 , (3, 0, None, None) , 0 , )),
	(( 'BuiltInFace' , 'pvarfBuiltIn' , ), 1610940416, (1610940416, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 336 , (3, 0, None, None) , 0 , )),
	(( 'CopyFace' , ), 1610940418, (1610940418, (), [ ], 1 , 1 , 4 , 0 , 340 , (3, 0, None, None) , 0 , )),
	(( 'FaceId' , 'pid' , ), 1610940419, (1610940419, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 344 , (3, 0, None, None) , 0 , )),
	(( 'FaceId' , 'pid' , ), 1610940419, (1610940419, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 348 , (3, 0, None, None) , 0 , )),
	(( 'PasteFace' , ), 1610940421, (1610940421, (), [ ], 1 , 1 , 4 , 0 , 352 , (3, 0, None, None) , 0 , )),
	(( 'ShortcutText' , 'pbstrText' , ), 1610940422, (1610940422, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 356 , (3, 0, None, None) , 0 , )),
	(( 'ShortcutText' , 'pbstrText' , ), 1610940422, (1610940422, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 360 , (3, 0, None, None) , 0 , )),
	(( 'State' , 'pstate' , ), 1610940424, (1610940424, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 364 , (3, 0, None, None) , 0 , )),
	(( 'State' , 'pstate' , ), 1610940424, (1610940424, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 368 , (3, 0, None, None) , 0 , )),
	(( 'Style' , 'pstyle' , ), 1610940426, (1610940426, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 372 , (3, 0, None, None) , 0 , )),
	(( 'Style' , 'pstyle' , ), 1610940426, (1610940426, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 376 , (3, 0, None, None) , 0 , )),
	(( 'HyperlinkType' , 'phlType' , ), 1610940428, (1610940428, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 380 , (3, 0, None, None) , 0 , )),
	(( 'HyperlinkType' , 'phlType' , ), 1610940428, (1610940428, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 384 , (3, 0, None, None) , 0 , )),
] _CommandBarComboBox_vtables_dispatch_ = 1 _CommandBarComboBox_vtables_ = [
	(( 'AddItem' , 'Text' , 'Index' , ), 1610940416, (1610940416, (), [ (8, 1, None, None) , 
			(12, 17, None, None) , ], 1 , 1 , 4 , 1 , 332 , (3, 0, None, None) , 0 , )),
	(( 'Clear' , ), 1610940417, (1610940417, (), [ ], 1 , 1 , 4 , 0 , 336 , (3, 0, None, None) , 0 , )),
	(( 'DropDownLines' , 'pcLines' , ), 1610940418, (1610940418, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 340 , (3, 0, None, None) , 0 , )),
	(( 'DropDownLines' , 'pcLines' , ), 1610940418, (1610940418, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 344 , (3, 0, None, None) , 0 , )),
	(( 'DropDownWidth' , 'pdx' , ), 1610940420, (1610940420, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 348 , (3, 0, None, None) , 0 , )),
	(( 'DropDownWidth' , 'pdx' , ), 1610940420, (1610940420, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 352 , (3, 0, None, None) , 0 , )),
	(( 'List' , 'Index' , 'pbstrItem' , ), 1610940422, (1610940422, (), [ (3, 1, None, None) , 
			(16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 356 , (3, 0, None, None) , 0 , )),
	(( 'List' , 'Index' , 'pbstrItem' , ), 1610940422, (1610940422, (), [ (3, 1, None, None) , 
			(8, 1, None, None) , ], 1 , 4 , 4 , 0 , 360 , (3, 0, None, None) , 0 , )),
	(( 'ListCount' , 'pcItems' , ), 1610940424, (1610940424, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 364 , (3, 0, None, None) , 0 , )),
	(( 'ListHeaderCount' , 'pcItems' , ), 1610940425, (1610940425, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 368 , (3, 0, None, None) , 0 , )),
	(( 'ListHeaderCount' , 'pcItems' , ), 1610940425, (1610940425, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 372 , (3, 0, None, None) , 0 , )),
	(( 'ListIndex' , 'pi' , ), 1610940427, (1610940427, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 376 , (3, 0, None, None) , 0 , )),
	(( 'ListIndex' , 'pi' , ), 1610940427, (1610940427, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 380 , (3, 0, None, None) , 0 , )),
	(( 'RemoveItem' , 'Index' , ), 1610940429, (1610940429, (), [ (3, 1, None, None) , ], 1 , 1 , 4 , 0 , 384 , (3, 0, None, None) , 0 , )),
	(( 'Style' , 'pstyle' , ), 1610940430, (1610940430, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 388 , (3, 0, None, None) , 0 , )),
	(( 'Style' , 'pstyle' , ), 1610940430, (1610940430, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 392 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstrText' , ), 1610940432, (1610940432, (), [ (16392, 10, None, None) , ], 1 , 2 , 4 , 0 , 396 , (3, 0, None, None) , 0 , )),
	(( 'Text' , 'pbstrText' , ), 1610940432, (1610940432, (), [ (8, 1, None, None) , ], 1 , 4 , 4 , 0 , 400 , (3, 0, None, None) , 0 , )),
] _CommandBars_vtables_dispatch_ = 1 _CommandBars_vtables_ = [
	(( 'ActionControl' , 'ppcbc' , ), 1610809344, (1610809344, (), [ (16393, 10, None, "IID('{000C0308-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 36 , (3, 0, None, None) , 0 , )),
	(( 'ActiveMenuBar' , 'ppcb' , ), 1610809345, (1610809345, (), [ (16393, 10, None, "IID('{000C0304-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 40 , (3, 0, None, None) , 0 , )),
	(( 'Add' , 'Name' , 'Position' , 'MenuBar' , 'Temporary' , 
			'ppcb' , ), 1610809346, (1610809346, (), [ (12, 17, None, None) , (12, 17, None, None) , (12, 17, None, None) , 
			(12, 17, None, None) , (16393, 10, None, "IID('{000C0304-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 4 , 44 , (3, 0, None, None) , 0 , )),
	(( 'Count' , 'pcToolbars' , ), 1610809347, (1610809347, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 48 , (3, 0, None, None) , 0 , )),
	(( 'DisplayTooltips' , 'pvarfDisplayTooltips' , ), 1610809348, (1610809348, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 52 , (3, 0, None, None) , 0 , )),
	(( 'DisplayTooltips' , 'pvarfDisplayTooltips' , ), 1610809348, (1610809348, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 56 , (3, 0, None, None) , 0 , )),
	(( 'DisplayKeysInTooltips' , 'pvarfDisplayKeys' , ), 1610809350, (1610809350, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 60 , (3, 0, None, None) , 0 , )),
	(( 'DisplayKeysInTooltips' , 'pvarfDisplayKeys' , ), 1610809350, (1610809350, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 64 , (3, 0, None, None) , 0 , )),
	(( 'FindControl' , 'Type' , 'Id' , 'Tag' , 'Visible' , 
			'ppcbc' , ), 1610809352, (1610809352, (), [ (12, 17, None, None) , (12, 17, None, None) , (12, 17, None, None) , 
			(12, 17, None, None) , (16393, 10, None, "IID('{000C0308-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 4 , 68 , (3, 0, None, None) , 0 , )),
	(( 'Item' , 'Index' , 'ppcb' , ), 0, (0, (), [ (12, 1, None, None) , 
			(16393, 10, None, "IID('{000C0304-0000-0000-C000-000000000046}')") , ], 1 , 2 , 4 , 0 , 72 , (3, 0, None, None) , 0 , )),
	(( 'LargeButtons' , 'pvarfLargeButtons' , ), 1610809354, (1610809354, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 76 , (3, 0, None, None) , 0 , )),
	(( 'LargeButtons' , 'pvarfLargeButtons' , ), 1610809354, (1610809354, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 80 , (3, 0, None, None) , 0 , )),
	(( 'MenuAnimationStyle' , 'pma' , ), 1610809356, (1610809356, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 84 , (3, 0, None, None) , 0 , )),
	(( 'MenuAnimationStyle' , 'pma' , ), 1610809356, (1610809356, (), [ (3, 1, None, None) , ], 1 , 4 , 4 , 0 , 88 , (3, 0, None, None) , 0 , )),
	(( '_NewEnum' , 'ppienum' , ), -4, (-4, (), [ (16397, 10, None, None) , ], 1 , 2 , 4 , 0 , 92 , (3, 0, None, None) , 1024 , )),
	(( 'Parent' , 'ppidisp' , ), 1610809359, (1610809359, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 96 , (3, 0, None, None) , 0 , )),
	(( 'ReleaseFocus' , ), 1610809360, (1610809360, (), [ ], 1 , 1 , 4 , 0 , 100 , (3, 0, None, None) , 0 , )),
	(( 'IdsString' , 'ids' , 'pbstrName' , 'pcch' , ), 1610809361, (1610809361, (), [ 
			(3, 1, None, None) , (16392, 2, None, None) , (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 104 , (3, 0, None, None) , 64 , )),
	(( 'TmcGetName' , 'tmc' , 'pbstrName' , 'pcch' , ), 1610809362, (1610809362, (), [ 
			(3, 1, None, None) , (16392, 2, None, None) , (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 108 , (3, 0, None, None) , 64 , )),
	(( 'AdaptiveMenus' , 'pvarfAdaptiveMenus' , ), 1610809363, (1610809363, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'AdaptiveMenus' , 'pvarfAdaptiveMenus' , ), 1610809363, (1610809363, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
	(( 'FindControls' , 'Type' , 'Id' , 'Tag' , 'Visible' , 
			'ppcbcs' , ), 1610809365, (1610809365, (), [ (12, 17, None, None) , (12, 17, None, None) , (12, 17, None, None) , 
			(12, 17, None, None) , (16393, 10, None, "IID('{000C0306-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 4 , 120 , (3, 0, None, None) , 0 , )),
	(( 'AddEx' , 'TbidOrName' , 'Position' , 'MenuBar' , 'Temporary' , 
			'TbtrProtection' , 'ppcb' , ), 1610809366, (1610809366, (), [ (12, 17, None, None) , (12, 17, None, None) , 
			(12, 17, None, None) , (12, 17, None, None) , (12, 17, None, None) , (16393, 10, None, "IID('{000C0304-0000-0000-C000-000000000046}')") , ], 1 , 1 , 4 , 5 , 124 , (3, 0, None, None) , 64 , )),
	(( 'DisplayFonts' , 'pvarfDisplayFonts' , ), 1610809367, (1610809367, (), [ (16395, 10, None, None) , ], 1 , 2 , 4 , 0 , 128 , (3, 0, None, None) , 0 , )),
	(( 'DisplayFonts' , 'pvarfDisplayFonts' , ), 1610809367, (1610809367, (), [ (11, 1, None, None) , ], 1 , 4 , 4 , 0 , 132 , (3, 0, None, None) , 0 , )),
] _IMsoDispObj_vtables_dispatch_ = 1 _IMsoDispObj_vtables_ = [
	(( 'Application' , 'ppidisp' , ), 1610743808, (1610743808, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 28 , (3, 0, None, None) , 0 , )),
	(( 'Creator' , 'plCreator' , ), 1610743809, (1610743809, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 32 , (3, 0, None, None) , 0 , )),
] _IMsoOleAccDispObj_vtables_dispatch_ = 1 _IMsoOleAccDispObj_vtables_ = [
	(( 'Application' , 'ppidisp' , ), 1610809344, (1610809344, (), [ (16393, 10, None, None) , ], 1 , 2 , 4 , 0 , 112 , (3, 0, None, None) , 0 , )),
	(( 'Creator' , 'plCreator' , ), 1610809345, (1610809345, (), [ (16387, 10, None, None) , ], 1 , 2 , 4 , 0 , 116 , (3, 0, None, None) , 0 , )),
] RecordMap = {
} CLSIDToClassMap = {
	'{000C0340-0000-0000-C000-000000000046}' : Scripts,
	'{000C0341-0000-0000-C000-000000000046}' : Script,
	'{2DF8D04D-5BFA-101B-BDE5-00AA0044DE52}' : DocumentProperties,
	'{2DF8D04E-5BFA-101B-BDE5-00AA0044DE52}' : DocumentProperty,
	'{000C0913-0000-0000-C000-000000000046}' : WebPageFont,
	'{000C0351-0000-0000-C000-000000000046}' : _CommandBarButtonEvents,
	'{000C0352-0000-0000-C000-000000000046}' : _CommandBarsEvents,
	'{000C0353-0000-0000-C000-000000000046}' : LanguageSettings,
	'{000C0354-0000-0000-C000-000000000046}' : _CommandBarComboBoxEvents,
	'{55F88891-7708-11D1-ACEB-006008961DA5}' : CommandBarButton,
	'{000C0357-0000-0000-C000-000000000046}' : HTMLProjectItems,
	'{000C0358-0000-0000-C000-000000000046}' : HTMLProjectItem,
	'{000C0359-0000-0000-C000-000000000046}' : IMsoDispCagNotifySink,
	'{000C035A-0000-0000-C000-000000000046}' : MsoDebugOptions,
	'{618736E0-3C3D-11CF-810C-00AA00389B71}' : IAccessible,
	'{000C0360-0000-0000-C000-000000000046}' : AnswerWizard,
	'{000C0361-0000-0000-C000-000000000046}' : AnswerWizardFiles,
	'{000C0914-0000-0000-C000-000000000046}' : WebPageFonts,
	'{55F88897-7708-11D1-ACEB-006008961DA5}' : CommandBarComboBox,
	'{55F88892-7708-11D1-ACEB-006008961DA5}' : ICommandBarsEvents,
	'{000C0356-0000-0000-C000-000000000046}' : HTMLProject,
	'{000C0300-0000-0000-C000-000000000046}' : _IMsoDispObj,
	'{000C0301-0000-0000-C000-000000000046}' : _IMsoOleAccDispObj,
	'{000C0302-0000-0000-C000-000000000046}' : _CommandBars,
	'{000C0304-0000-0000-C000-000000000046}' : CommandBar,
	'{000C0306-0000-0000-C000-000000000046}' : CommandBarControls,
	'{000C0308-0000-0000-C000-000000000046}' : CommandBarControl,
	'{55F88890-7708-11D1-ACEB-006008961DA5}' : ICommandBarButtonEvents,
	'{000C030A-0000-0000-C000-000000000046}' : CommandBarPopup,
	'{000C030C-0000-0000-C000-000000000046}' : _CommandBarComboBox,
	'{000C030D-0000-0000-C000-000000000046}' : _CommandBarActiveX,
	'{000C030E-0000-0000-C000-000000000046}' : _CommandBarButton,
	'{55F88893-7708-11D1-ACEB-006008961DA5}' : CommandBars,
	'{000C0310-0000-0000-C000-000000000046}' : Adjustments,
	'{000C0311-0000-0000-C000-000000000046}' : CalloutFormat,
	'{000C0312-0000-0000-C000-000000000046}' : ColorFormat,
	'{000C0313-0000-0000-C000-000000000046}' : ConnectorFormat,
	'{000C0314-0000-0000-C000-000000000046}' : FillFormat,
	'{000C0315-0000-0000-C000-000000000046}' : FreeformBuilder,
	'{000C0316-0000-0000-C000-000000000046}' : GroupShapes,
	'{000C0317-0000-0000-C000-000000000046}' : LineFormat,
	'{000C0318-0000-0000-C000-000000000046}' : ShapeNode,
	'{000C0319-0000-0000-C000-000000000046}' : ShapeNodes,
	'{000C031A-0000-0000-C000-000000000046}' : PictureFormat,
	'{000C031B-0000-0000-C000-000000000046}' : ShadowFormat,
	'{000C031C-0000-0000-C000-000000000046}' : Shape,
	'{000C031D-0000-0000-C000-000000000046}' : ShapeRange,
	'{000C031E-0000-0000-C000-000000000046}' : Shapes,
	'{000C031F-0000-0000-C000-000000000046}' : TextEffectFormat,
	'{000C0320-0000-0000-C000-000000000046}' : TextFrame,
	'{000C0321-0000-0000-C000-000000000046}' : ThreeDFormat,
	'{000C0322-0000-0000-C000-000000000046}' : Assistant,
	'{000C0324-0000-0000-C000-000000000046}' : Balloon,
	'{000C0326-0000-0000-C000-000000000046}' : BalloonCheckboxes,
	'{000C0328-0000-0000-C000-000000000046}' : BalloonCheckbox,
	'{55F88896-7708-11D1-ACEB-006008961DA5}' : ICommandBarComboBoxEvents,
	'{000C032E-0000-0000-C000-000000000046}' : BalloonLabels,
	'{000C0330-0000-0000-C000-000000000046}' : BalloonLabel,
	'{000C0331-0000-0000-C000-000000000046}' : FoundFiles,
	'{000C0332-0000-0000-C000-000000000046}' : FileSearch,
	'{000C0333-0000-0000-C000-000000000046}' : PropertyTest,
	'{000C0334-0000-0000-C000-000000000046}' : PropertyTests,
	'{000C0337-0000-0000-C000-000000000046}' : IFind,
	'{000C0338-0000-0000-C000-000000000046}' : IFoundFiles,
	'{000C0339-0000-0000-C000-000000000046}' : COMAddIns,
	'{000C033A-0000-0000-C000-000000000046}' : COMAddIn,
} CLSIDToPackageMap = {} win32com.client.CLSIDToClass.RegisterCLSIDsFromDict( CLSIDToClassMap ) VTablesToPackageMap = {} VTablesToClassMap = {
	'{000C0340-0000-0000-C000-000000000046}' : 'Scripts',
	'{000C0341-0000-0000-C000-000000000046}' : 'Script',
	'{000C0913-0000-0000-C000-000000000046}' : 'WebPageFont',
	'{000C0353-0000-0000-C000-000000000046}' : 'LanguageSettings',
	'{000C0356-0000-0000-C000-000000000046}' : 'HTMLProject',
	'{000C0357-0000-0000-C000-000000000046}' : 'HTMLProjectItems',
	'{000C0358-0000-0000-C000-000000000046}' : 'HTMLProjectItem',
	'{000C0359-0000-0000-C000-000000000046}' : 'IMsoDispCagNotifySink',
	'{000C035A-0000-0000-C000-000000000046}' : 'MsoDebugOptions',
	'{618736E0-3C3D-11CF-810C-00AA00389B71}' : 'IAccessible',
	'{000C0360-0000-0000-C000-000000000046}' : 'AnswerWizard',
	'{000C0361-0000-0000-C000-000000000046}' : 'AnswerWizardFiles',
	'{000C0914-0000-0000-C000-000000000046}' : 'WebPageFonts',
	'{55F88892-7708-11D1-ACEB-006008961DA5}' : 'ICommandBarsEvents',
	'{000C0300-0000-0000-C000-000000000046}' : '_IMsoDispObj',
	'{000C0301-0000-0000-C000-000000000046}' : '_IMsoOleAccDispObj',
	'{000C0302-0000-0000-C000-000000000046}' : '_CommandBars',
	'{000C0304-0000-0000-C000-000000000046}' : 'CommandBar',
	'{000C0306-0000-0000-C000-000000000046}' : 'CommandBarControls',
	'{000C0308-0000-0000-C000-000000000046}' : 'CommandBarControl',
	'{55F88890-7708-11D1-ACEB-006008961DA5}' : 'ICommandBarButtonEvents',
	'{000C030A-0000-0000-C000-000000000046}' : 'CommandBarPopup',
	'{000C030C-0000-0000-C000-000000000046}' : '_CommandBarComboBox',
	'{000C030D-0000-0000-C000-000000000046}' : '_CommandBarActiveX',
	'{000C030E-0000-0000-C000-000000000046}' : '_CommandBarButton',
	'{000C0310-0000-0000-C000-000000000046}' : 'Adjustments',
	'{000C0311-0000-0000-C000-000000000046}' : 'CalloutFormat',
	'{000C0312-0000-0000-C000-000000000046}' : 'ColorFormat',
	'{000C0313-0000-0000-C000-000000000046}' : 'ConnectorFormat',
	'{000C0314-0000-0000-C000-000000000046}' : 'FillFormat',
	'{000C0315-0000-0000-C000-000000000046}' : 'FreeformBuilder',
	'{000C0316-0000-0000-C000-000000000046}' : 'GroupShapes',
	'{000C0317-0000-0000-C000-000000000046}' : 'LineFormat',
	'{000C0318-0000-0000-C000-000000000046}' : 'ShapeNode',
	'{000C0319-0000-0000-C000-000000000046}' : 'ShapeNodes',
	'{000C031A-0000-0000-C000-000000000046}' : 'PictureFormat',
	'{000C031B-0000-0000-C000-000000000046}' : 'ShadowFormat',
	'{000C031C-0000-0000-C000-000000000046}' : 'Shape',
	'{000C031D-0000-0000-C000-000000000046}' : 'ShapeRange',
	'{000C031E-0000-0000-C000-000000000046}' : 'Shapes',
	'{000C031F-0000-0000-C000-000000000046}' : 'TextEffectFormat',
	'{000C0320-0000-0000-C000-000000000046}' : 'TextFrame',
	'{000C0321-0000-0000-C000-000000000046}' : 'ThreeDFormat',
	'{000C0322-0000-0000-C000-000000000046}' : 'Assistant',
	'{000C0324-0000-0000-C000-000000000046}' : 'Balloon',
	'{000C0326-0000-0000-C000-000000000046}' : 'BalloonCheckboxes',
	'{000C0328-0000-0000-C000-000000000046}' : 'BalloonCheckbox',
	'{55F88896-7708-11D1-ACEB-006008961DA5}' : 'ICommandBarComboBoxEvents',
	'{000C032E-0000-0000-C000-000000000046}' : 'BalloonLabels',
	'{000C0330-0000-0000-C000-000000000046}' : 'BalloonLabel',
	'{000C0331-0000-0000-C000-000000000046}' : 'FoundFiles',
	'{000C0332-0000-0000-C000-000000000046}' : 'FileSearch',
	'{000C0333-0000-0000-C000-000000000046}' : 'PropertyTest',
	'{000C0334-0000-0000-C000-000000000046}' : 'PropertyTests',
	'{000C0337-0000-0000-C000-000000000046}' : 'IFind',
	'{000C0338-0000-0000-C000-000000000046}' : 'IFoundFiles',
	'{000C0339-0000-0000-C000-000000000046}' : 'COMAddIns',
	'{000C033A-0000-0000-C000-000000000046}' : 'COMAddIn',
} NamesToIIDMap = {
	'IFind' : '{000C0337-0000-0000-C000-000000000046}',
	'ICommandBarComboBoxEvents' : '{55F88896-7708-11D1-ACEB-006008961DA5}',
	'Script' : '{000C0341-0000-0000-C000-000000000046}',
	'CommandBar' : '{000C0304-0000-0000-C000-000000000046}',
	'BalloonLabels' : '{000C032E-0000-0000-C000-000000000046}',
	'Balloon' : '{000C0324-0000-0000-C000-000000000046}',
	'Shapes' : '{000C031E-0000-0000-C000-000000000046}',
	'CommandBarPopup' : '{000C030A-0000-0000-C000-000000000046}',
	'MsoDebugOptions' : '{000C035A-0000-0000-C000-000000000046}',
	'WebPageFont' : '{000C0913-0000-0000-C000-000000000046}',
	'CommandBarControls' : '{000C0306-0000-0000-C000-000000000046}',
	'ColorFormat' : '{000C0312-0000-0000-C000-000000000046}',
	'TextFrame' : '{000C0320-0000-0000-C000-000000000046}',
	'ThreeDFormat' : '{000C0321-0000-0000-C000-000000000046}',
	'FoundFiles' : '{000C0331-0000-0000-C000-000000000046}',
	'ICommandBarButtonEvents' : '{55F88890-7708-11D1-ACEB-006008961DA5}',
	'IAccessible' : '{618736E0-3C3D-11CF-810C-00AA00389B71}',
	'Assistant' : '{000C0322-0000-0000-C000-000000000046}',
	'AnswerWizard' : '{000C0360-0000-0000-C000-000000000046}',
	'ShapeRange' : '{000C031D-0000-0000-C000-000000000046}',
	'_CommandBarActiveX' : '{000C030D-0000-0000-C000-000000000046}',
	'FillFormat' : '{000C0314-0000-0000-C000-000000000046}',
	'ShapeNodes' : '{000C0319-0000-0000-C000-000000000046}',
	'ConnectorFormat' : '{000C0313-0000-0000-C000-000000000046}',
	'_CommandBarButtonEvents' : '{000C0351-0000-0000-C000-000000000046}',
	'_CommandBarComboBoxEvents' : '{000C0354-0000-0000-C000-000000000046}',
	'Scripts' : '{000C0340-0000-0000-C000-000000000046}',
	'BalloonLabel' : '{000C0330-0000-0000-C000-000000000046}',
	'GroupShapes' : '{000C0316-0000-0000-C000-000000000046}',
	'DocumentProperty' : '{2DF8D04E-5BFA-101B-BDE5-00AA0044DE52}',
	'HTMLProjectItems' : '{000C0357-0000-0000-C000-000000000046}',
	'LanguageSettings' : '{000C0353-0000-0000-C000-000000000046}',
	'_CommandBarButton' : '{000C030E-0000-0000-C000-000000000046}',
	'ShadowFormat' : '{000C031B-0000-0000-C000-000000000046}',
	'TextEffectFormat' : '{000C031F-0000-0000-C000-000000000046}',
	'_CommandBars' : '{000C0302-0000-0000-C000-000000000046}',
	'ICommandBarsEvents' : '{55F88892-7708-11D1-ACEB-006008961DA5}',
	'COMAddIns' : '{000C0339-0000-0000-C000-000000000046}',
	'WebPageFonts' : '{000C0914-0000-0000-C000-000000000046}',
	'HTMLProjectItem' : '{000C0358-0000-0000-C000-000000000046}',
	'AnswerWizardFiles' : '{000C0361-0000-0000-C000-000000000046}',
	'DocumentProperties' : '{2DF8D04D-5BFA-101B-BDE5-00AA0044DE52}',
	'PictureFormat' : '{000C031A-0000-0000-C000-000000000046}',
	'FileSearch' : '{000C0332-0000-0000-C000-000000000046}',
	'IMsoDispCagNotifySink' : '{000C0359-0000-0000-C000-000000000046}',
	'COMAddIn' : '{000C033A-0000-0000-C000-000000000046}',
	'FreeformBuilder' : '{000C0315-0000-0000-C000-000000000046}',
	'BalloonCheckboxes' : '{000C0326-0000-0000-C000-000000000046}',
	'PropertyTests' : '{000C0334-0000-0000-C000-000000000046}',
	'_CommandBarsEvents' : '{000C0352-0000-0000-C000-000000000046}',
	'IFoundFiles' : '{000C0338-0000-0000-C000-000000000046}',
	'_IMsoOleAccDispObj' : '{000C0301-0000-0000-C000-000000000046}',
	'ShapeNode' : '{000C0318-0000-0000-C000-000000000046}',
	'PropertyTest' : '{000C0333-0000-0000-C000-000000000046}',
	'Adjustments' : '{000C0310-0000-0000-C000-000000000046}',
	'BalloonCheckbox' : '{000C0328-0000-0000-C000-000000000046}',
	'HTMLProject' : '{000C0356-0000-0000-C000-000000000046}',
	'Shape' : '{000C031C-0000-0000-C000-000000000046}',
	'CalloutFormat' : '{000C0311-0000-0000-C000-000000000046}',
	'CommandBarControl' : '{000C0308-0000-0000-C000-000000000046}',
	'LineFormat' : '{000C0317-0000-0000-C000-000000000046}',
	'_IMsoDispObj' : '{000C0300-0000-0000-C000-000000000046}',
	'_CommandBarComboBox' : '{000C030C-0000-0000-C000-000000000046}',
} win32com.client.constants.__dicts__.append(constants.__dict__)

