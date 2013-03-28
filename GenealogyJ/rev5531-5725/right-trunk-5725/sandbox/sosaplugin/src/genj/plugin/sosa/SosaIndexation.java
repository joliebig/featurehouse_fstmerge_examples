

package genj.plugin.sosa;





import genj.gedcom.Fam;
import genj.gedcom.Gedcom;



import genj.gedcom.Indi;
import genj.gedcom.Property;















import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;


	
	
	public class SosaIndexation {
	
		static final String SOSA_LABEL="_SOSA";
		static final String SOSA_INDEX_SEPARATOR=";";
		private final String emptySosaMarker="";
		static final int SOSA_INDEX_MARKER_LENGTH=5;
		
		
		
		
		private enum myExtendedSosaMarkerEnum {
			
			BIOLOGICAL_BROTHER_AND_SISTER("(  +)"),
			BIOLOGICAL_BROTHER_AND_SISTER_SPOUSE("( ++)"),
			OTHER_BROTHER_AND_SISTER("( ~+)"),
			OTHER_BROTHER_AND_SISTER_SPOUSE("(~++)");

			private String marker;

			
			myExtendedSosaMarkerEnum(String marker) {
				this.marker = marker;
			}

			
			public String getMarker() {
				return marker;
			}
		}

		private Map<Integer,Indi>myMap=new HashMap<Integer,Indi>();
		private	ArrayList<String>myList=new ArrayList<String>();
		private Logger LOG = Logger.getLogger("genj.plugin.sosa");
		private enum interactionType{_NULL,_SOSACutFromINDI,_SOSAAddedToINDI,_SOSAModifiedInINDI,_SOSADeletedFromINDI,_SOSASetValueToINDI,_CHILCutFromFAM,_CHILAddedToFAM,_newINDIInFAM,_newFAM};
		private interactionType action=interactionType._NULL;

		private Indi mySosaRoot;
		private Gedcom gedcom;
		
		private String sosaIndexArray[];
		private Map<Integer,Indi> sosaIndexIndiMap=new TreeMap<Integer,Indi>();
		
		
		public SosaIndexation(Indi mySosaRoot,Gedcom gedcom) {
			this.mySosaRoot=mySosaRoot;
			this.gedcom=gedcom;
			if (mySosaRoot != null) {
			setSosaIndexation(mySosaRoot);
			}
			LOG.fine("Sosa indexation mise dans les données = "+mySosaRoot);
			LOG.fine("=========Gedcom= "+gedcom);
		}
		
		
		
		public void setSosaIndexation(Indi indi) {
			
			if (myMap.size() !=0 ) myMap.clear();
			
			int sosaIndex=1;
			
			removeSosaIndexationFromAllIndis();
			
			
			buildSosaIndexation(mySosaRoot,sosaIndex);
			
			sosaIndexIndiMap=new TreeMap<Integer,Indi>(myMap);
			
			if (myList.size() !=0 ) myList.removeAll(myList);
			
			for (Map.Entry <Integer,Indi> entry :sosaIndexIndiMap.entrySet()) {
				myList.add(Integer.toString(entry.getKey()));
			}
			
			sosaIndexArray=myList.toArray(new String[myList.size()]);
		}
		
		
		public void removeSosaIndexationFromAllIndis() {
			
			Property SosaProperties[];
			Indi indi;
			Collection indisCollection=gedcom.getEntities(Gedcom.INDI);
			for (Iterator it=indisCollection.iterator();it.hasNext();) {
				indi=(Indi)it.next();
				
				
				SosaProperties=indi.getProperties(SOSA_LABEL);
				for (int i=0;i<SosaProperties.length;i++) {
					
					indi.delProperty(SosaProperties[i]);
				}
			}
		}

		
		public void removeSosaIndexationFromIndi(Indi indi,int sosaIndex) {
			Indi indis[],spouses[],children[];
			Fam famc,fams[];
			
			myMap.remove(sosaIndex);
			
			deleteSosaIndexFromIndi(indi);
			
			if (isExtendSosaIndexation() == true) {
				
				indis=indi.getSiblings(false);
				for (int i=0;i<indis.length;i++) {
					
					deleteSosaIndexFromIndi(indis[i]);
					
					
					spouses=indis[i].getPartners();
					for (int j=0;j<spouses.length;j++) {
						
						deleteSosaIndexFromIndi(spouses[j]);
					}
				}
			}
			
			Indi father=indi.getBiologicalFather();
			
			Indi mother=indi.getBiologicalMother();
			
			famc=indi.getFamilyWhereBiologicalChild();
			
			if (isExtendSosaIndexation() == true) {
				
				if (father != null) {
					
					
					fams=father.getFamiliesWhereSpouse();
					for (int i=0;i<fams.length;i++) {
						
						if (fams[i] != famc) {
							
							children=fams[i].getChildren();
							for (int j=0;j<children.length;j++) {
								
								deleteSosaIndexFromIndi(children[j]);
								
								spouses=children[j].getPartners();
								for (int k=0;k<spouses.length;k++) {
									
									deleteSosaIndexFromIndi(spouses[k]);
								}
							}
						}
					}
				}
				
				if (mother != null) {
					
					fams=mother.getFamiliesWhereSpouse();
					for (int i=0;i<fams.length;i++) {
						
						if (fams[i] != famc) {
							
							children=fams[i].getChildren();
							for (int j=0;j<children.length;j++) {
								
								deleteSosaIndexFromIndi(children[j]);
								
								spouses=children[j].getPartners();
								for (int k=0;k<spouses.length;k++) {
									
									deleteSosaIndexFromIndi(spouses[k]);
								}
							}
						}
					}
				}
			}
			
			sosaIndex=2*sosaIndex;
			
			if (father != null) {
				
				removeSosaIndexationFromIndi(father,sosaIndex);
			}
			
			sosaIndex++;
			
			if (mother != null) {
				
				removeSosaIndexationFromIndi(mother,sosaIndex);
			}
		}

		 
		private void deleteSosaIndexFromIndi(Indi indi) {
			
			
			
			
			
			Property sosaProperty=indi.getProperty(SOSA_LABEL);
			if (sosaProperty == null) {
				
			}
			else {
				
				indi.delProperty(sosaProperty);
			}
		}
		
		public void deleteExistingSosaIndexFromIndi(Indi indi,Property sosaProperty) {
			
			
			
			
			
			
			
			
			
			
				
				indi.delProperty(sosaProperty);
			
		}
		
		
		public void removeSosaTagFromIndi(Indi indi,Property sosaProperty) {
			
			LOG.fine("juste avant effacement de _SOSA");
			indi.delProperty(sosaProperty);
			LOG.fine("juste après effacement de _SOSA");
		}

		

		private void buildSosaIndexation(Indi indi,int sosaIndex) {
			Indi indis[],spouses[],children[];
			Fam famc,fams[];
			
			
			
			myMap.put(sosaIndex,indi);
			
			
			setSosaIndexToIndi(indi,sosaIndex,emptySosaMarker);
			
			if (isExtendSosaIndexation() == true) {
				
				indis=indi.getSiblings(false);
				for (int i=0;i<indis.length;i++) {
					
					
					setSosaIndexToIndi(indis[i],sosaIndex,myExtendedSosaMarkerEnum.BIOLOGICAL_BROTHER_AND_SISTER.getMarker());
					
					
					spouses=indis[i].getPartners();
					for (int j=0;j<spouses.length;j++) {
						
						
						setSosaIndexToIndi(spouses[j],sosaIndex,myExtendedSosaMarkerEnum.BIOLOGICAL_BROTHER_AND_SISTER_SPOUSE.getMarker());
					}
				}
			}
			
			Indi father=indi.getBiologicalFather();
			
			Indi mother=indi.getBiologicalMother();
			
			famc=indi.getFamilyWhereBiologicalChild();
			
			if (isExtendSosaIndexation() == true) {
				
				if (father != null) {
					
					
					fams=father.getFamiliesWhereSpouse();
					for (int i=0;i<fams.length;i++) {
						
						if (fams[i] != famc) {
							
							children=fams[i].getChildren();
							for (int j=0;j<children.length;j++) {
								
								
								setSosaIndexToIndi(children[j],sosaIndex,myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER.getMarker());
								
								spouses=children[j].getPartners();
								for (int k=0;k<spouses.length;k++) {
									
									
									setSosaIndexToIndi(spouses[k],sosaIndex,myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER_SPOUSE.getMarker());
									

								}
							}
						}
					}
				}
				
				if (mother != null) {
					
					fams=mother.getFamiliesWhereSpouse();
					for (int i=0;i<fams.length;i++) {
						
						if (fams[i] != famc) {
							
							children=fams[i].getChildren();
							for (int j=0;j<children.length;j++) {
								
								
								setSosaIndexToIndi(children[j],sosaIndex,myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER.getMarker());
								
								spouses=children[j].getPartners();
								for (int k=0;k<spouses.length;k++) {
									
									
									setSosaIndexToIndi(spouses[k],sosaIndex,myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER_SPOUSE.getMarker());
								}
							}
						}
					}
				}
			}
			
			sosaIndex=2*sosaIndex;
			
			if (father != null) {
				
				buildSosaIndexation(father,sosaIndex);
			}
			
			sosaIndex++;
			
			if (mother != null) {
				
				buildSosaIndexation(mother,sosaIndex);
			}
		}


		 
		private void setSosaIndexToIndi(Indi indi,int sosaIndex,String sosaMarker) {
			String sosaIndex1=sosaMarker+String.valueOf(sosaIndex);
			
			
			
			
			
			Property SosaProperty=indi.getProperty(SOSA_LABEL);
			if (SosaProperty == null) {
				
				SosaProperty=indi.addProperty(SOSA_LABEL,sosaIndex1);
			}
			else {
				
				
				
				SosaProperty.setValue(SosaProperty.getValue()+SOSA_INDEX_SEPARATOR+sosaIndex1);
			}
		}

		 
		public void restoreSosaValueToIndi(Indi indi,String sosaValue) {
			indi.addProperty(SOSA_LABEL,sosaValue);
		}
		
		 

		public void restoreSosaInChildCutFromFam(Indi indi,Fam fam) {
			LOG.fine("CHILD : "+indi+" cut from FAM : "+fam);
			Property sosaProperty=indi.getProperty(SOSA_LABEL);
			
			String newIndex="";
			
			if (sosaProperty != null) {
				
				String sosaIndex=sosaProperty.getValue();
				
				int indexBeginning=0;
				
				int indexEnd=sosaIndex.indexOf(SOSA_INDEX_SEPARATOR);
				while (indexEnd != -1) {
					newIndex=buildNewSosaIndex("cutCHILFromFAM",indi,fam,sosaIndex.substring(indexBeginning,indexEnd),newIndex);
					indexBeginning=indexEnd+1;
					
					indexEnd=sosaIndex.indexOf(SOSA_INDEX_SEPARATOR,indexBeginning);
				}
				newIndex=buildNewSosaIndex("cutCHILFromFAM",indi,fam,sosaIndex.substring(indexBeginning),newIndex);
				
				
				if (newIndex.length() == 0) {
					
					action=interactionType._SOSADeletedFromINDI;
					indi.delProperty(sosaProperty);
					
				} else {
					
					action=interactionType._SOSAModifiedInINDI;
					sosaProperty.setValue(newIndex);
					
				}
			} else {
				
				LOG.fine("we do nothing");
			}
			
			Indi[] spouses=indi.getPartners();
			for (int i = 0; i < spouses.length; i++) {
				LOG.fine("épouse :" + spouses[i]);
				Property spouseSosaProperty = spouses[i].getProperty(SOSA_LABEL);
				if (spouseSosaProperty != null) {
					
					String spouseSosaIndex=spouseSosaProperty.getValue();
					String spouseSosaIndexToBeProcessed;
					int indexBeginning=0;
					
					int indexEnd=spouseSosaIndex.indexOf(SOSA_INDEX_SEPARATOR);
					while (indexEnd != -1) {
						spouseSosaIndexToBeProcessed=processImpactOfSpouseOnSosaIndexOfBrotherAndSisterOfIndi("cutCHILFromFAM",spouses[i],fam,spouseSosaIndex.substring(indexBeginning,indexEnd));
						if (spouseSosaIndexToBeProcessed.length() != 0) {
							LOG.fine("we have to process impact of index :"+spouseSosaIndexToBeProcessed);
						}
						indexBeginning=indexEnd+1;
						
						indexEnd=spouseSosaIndex.indexOf(SOSA_INDEX_SEPARATOR,indexBeginning);
					}
					spouseSosaIndexToBeProcessed=processImpactOfSpouseOnSosaIndexOfBrotherAndSisterOfIndi("cutCHILFromFAM",spouses[i],fam,spouseSosaIndex.substring(indexBeginning));
					if (spouseSosaIndexToBeProcessed.length() != 0) {
						LOG.fine("we have to process impact of index :"+spouseSosaIndexToBeProcessed);
					}
				}
			}
	}
		
		public String buildNewSosaIndex(String actionType, Indi indi,Fam fam,String indiSosaSubIndex,String indiNewSosaIndex) {
			LOG.fine("_SOSA partial= "+indiSosaSubIndex);
			String indiLinkIndex;
			String biologicalBrotherAndSisterSpouseSosaMarker=myExtendedSosaMarkerEnum.BIOLOGICAL_BROTHER_AND_SISTER_SPOUSE.getMarker();
			String biologicalBrotherAndSisterSosaMarker=myExtendedSosaMarkerEnum.BIOLOGICAL_BROTHER_AND_SISTER.getMarker();
			String otherBrotherAndSisterSpouseSosaMarker=myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER_SPOUSE.getMarker();
			String otherBrotherAndSisterSosaMarker=myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER.getMarker();
			if (actionType.equals("cutCHILFromFAM")) {
				if (isExtendSosaIndexation()) {
					String beginning=indiSosaSubIndex.substring(0,SOSA_INDEX_MARKER_LENGTH);
					
					if (indiSosaSubIndex.startsWith(biologicalBrotherAndSisterSpouseSosaMarker)) {
						LOG.fine(indiSosaSubIndex+" commence par :"+biologicalBrotherAndSisterSpouseSosaMarker);
						
						indiLinkIndex=indiSosaSubIndex.substring(biologicalBrotherAndSisterSpouseSosaMarker.length());
						
						LOG.fine("this indi is spouse of brother or sister of _SOSA= "+indiLinkIndex);
						LOG.fine("this indi remains as such");
					} else {
						
						if (indiSosaSubIndex.startsWith(biologicalBrotherAndSisterSosaMarker)) {
							LOG.fine(indiSosaSubIndex+" commence par :"+biologicalBrotherAndSisterSosaMarker);
							
							indiLinkIndex=indiSosaSubIndex.substring(biologicalBrotherAndSisterSosaMarker.length());
							
							LOG.fine("this indi is brother or sister of _SOSA= "+indiLinkIndex);
							LOG.fine("this indi is no more");
							indiSosaSubIndex="";
							
							Indi[] spouses=indi.getPartners();
							for (int k=0;k<spouses.length;k++) {
								LOG.fine("épouse :"+spouses[k]);
								
								Property spouseSosaProperty=spouses[k].getProperty(SOSA_LABEL);
								
								if (spouseSosaProperty != null) {
									
									String spouseSosaIndex=spouseSosaProperty.getValue();
									LOG.fine("spouseSosaIndex avant= "+spouseSosaIndex);
									
									
									int i0=spouseSosaIndex.indexOf(biologicalBrotherAndSisterSpouseSosaMarker+indiLinkIndex);
									int i1=i0+(biologicalBrotherAndSisterSpouseSosaMarker+indiLinkIndex).length();
									
									
									
									
									if (i1 != (spouseSosaIndex.length())) {
										i1+=1;
										
									} else {
										
										if (i0 != 0) {
											i0+=-1;
											
										} 
											
										
									}
									String spouseStrippedIndex=spouseSosaIndex.substring(i0,i1);
									
									spouseSosaIndex=spouseSosaIndex.replaceFirst("\\Q"+spouseStrippedIndex+"\\E","");
									
									if (spouseSosaIndex.length() == 0) {
										
										action=interactionType._SOSADeletedFromINDI;
										spouses[k].delProperty(spouseSosaProperty);
										
									} else {
										
										
										
										action=interactionType._SOSAModifiedInINDI;
										spouseSosaProperty.setValue(spouseSosaIndex);
										
									}
								} else {
									
									LOG.fine("ERREUR !");
								}
							}
						} else {
							
							if (indiSosaSubIndex.startsWith(otherBrotherAndSisterSpouseSosaMarker)) {
								LOG.fine(indiSosaSubIndex+" commence par :"+otherBrotherAndSisterSpouseSosaMarker);
								
								indiLinkIndex=indiSosaSubIndex.substring(otherBrotherAndSisterSpouseSosaMarker.length());
								
								LOG.fine("this indi is spouse of indi that same father or mother of _SOSA= "+indiLinkIndex);
								LOG.fine("this indi remains as such");
							} else {
								
								if (indiSosaSubIndex.startsWith(otherBrotherAndSisterSosaMarker)) {
									LOG.fine(indiSosaSubIndex+" commence par :"+otherBrotherAndSisterSosaMarker);
									
									indiLinkIndex=indiSosaSubIndex.substring(otherBrotherAndSisterSosaMarker.length());
									
									LOG.fine("this indi has same father or mother of _SOSA= "+indiLinkIndex);
									LOG.fine("this indi remains as such");
								}
							}
						}
					}
				} else {
					
					LOG.fine("Regular index");
				}
				LOG.fine("BILAN "+indiSosaSubIndex);
				
				if (indiSosaSubIndex.length() != 0) {
					if (indiNewSosaIndex.length() != 0) {
						LOG.fine("New index 1="+indiNewSosaIndex+SOSA_INDEX_SEPARATOR+indiSosaSubIndex);
						
						return indiNewSosaIndex=indiNewSosaIndex+SOSA_INDEX_SEPARATOR+indiSosaSubIndex;
					} else {
						LOG.fine("New index 2="+indiSosaSubIndex);
						return indiNewSosaIndex=indiSosaSubIndex;
					}
				} else {
					LOG.fine("New index 3="+indiNewSosaIndex);
					return indiNewSosaIndex;
				}
			} else {
				
				LOG.fine("New index 4="+indiNewSosaIndex);
				return indiNewSosaIndex;
			}
		}

		public String processImpactOfSpouseOnSosaIndexOfBrotherAndSisterOfIndi(String actionType,Indi indi,Fam fam,String spouseSosaSubIndex) {
			LOG.fine("_SOSA partial= "+spouseSosaSubIndex);
			if (actionType.equals("cutCHILFromFAM")) {
				if (isExtendSosaIndexation()) {
					
					
					if (spouseSosaSubIndex.startsWith(myExtendedSosaMarkerEnum.BIOLOGICAL_BROTHER_AND_SISTER_SPOUSE.getMarker())) {
						LOG.fine(spouseSosaSubIndex+" commence par :"+myExtendedSosaMarkerEnum.BIOLOGICAL_BROTHER_AND_SISTER_SPOUSE.getMarker());
						LOG.fine("we do nothing with= "+spouseSosaSubIndex);
						return "";
					} else {
						
						if (spouseSosaSubIndex.startsWith(myExtendedSosaMarkerEnum.BIOLOGICAL_BROTHER_AND_SISTER.getMarker())) {
							LOG.fine(spouseSosaSubIndex+" commence par :"+myExtendedSosaMarkerEnum.BIOLOGICAL_BROTHER_AND_SISTER.getMarker());
							LOG.fine("we do nothing with= "+spouseSosaSubIndex);
							return "";
						} else {
							
							if (spouseSosaSubIndex.startsWith(myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER_SPOUSE.getMarker())) {
								LOG.fine(spouseSosaSubIndex+" commence par :"+myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER_SPOUSE.getMarker());
								LOG.fine("we do nothing with= "+spouseSosaSubIndex);
								return "";
							} else {
								
								if (spouseSosaSubIndex.startsWith(myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER.getMarker())) {
									LOG.fine(spouseSosaSubIndex+" commence par :"+myExtendedSosaMarkerEnum.OTHER_BROTHER_AND_SISTER.getMarker());
									LOG.fine("we do nothing with= "+spouseSosaSubIndex);
									return "";
								} else {
									LOG.fine("we have to process impact of : "+spouseSosaSubIndex);
									return spouseSosaSubIndex;
								}
							}
						}
					}
				} else {
					
					
					LOG.fine("we have to process impact of : "+spouseSosaSubIndex);
					return spouseSosaSubIndex;
				}
			} else {
				LOG.fine("other cases to be processed");
				return "";
			}
		}

		public void restoreSosaInHusbCutFromFam(Indi indi,Fam fam) {
			LOG.fine("HUSB : "+indi+" cut from FAM : "+fam);
		}

		public void restoreSosaInWifeCutFromFam(Indi indi,Fam fam) {
			LOG.fine("WIFE : "+indi+" cut from FAM : "+fam);
		}

		public void restoreSosaInFamsCutFromMaleIndi(Fam fam,Indi indi) {
			LOG.fine("FAMS : "+fam+" cut from INDI : "+indi+" (M)");
		}

		public void restoreSosaInFamsCutFromFemaleIndi(Fam fam,Indi indi) {
			LOG.fine("FAMS : "+fam+"indiNewSosaIndexfrom INDI : "+indi+" (F)");
		}

		public void restoreSosaInFamcCutFromIndi(Fam fam,Indi indi) {
			
			LOG.fine("FAMC : "+fam+" cut  from INDI : "+indi);
		}

		public void restoreSosaInChildAddedToFam(Indi indi,Fam fam) {
			LOG.fine("INDI : "+indi+" created in FAM : "+fam);
		}
		
		public void addNewIndiToFam(Indi indi, Fam fam) {
			LOG.fine("INDI : "+indi+" created in FAM : "+fam);
		}
		
		
		public String[] getSosaIndexArray() {
			return sosaIndexArray;
		}

		
		public Map<Integer,Indi> getSosaMap() {
			return sosaIndexIndiMap;
		}
	
		
		public void setSosaGedcom(Gedcom gedcom) {
			this.gedcom=gedcom;
		}

		
		public void setSosaRoot(Indi indi) {
			LOG.fine("Sosa indexation mise dans les données = "+indi);
			this.mySosaRoot=indi;
		}

		
		public Indi getSosaRoot() {
			return mySosaRoot;
		}
		
		
		private boolean isExtendSosaIndexation() {
		  return SosaOptions.getInstance().isExtendSosaIndexation;
		}

	}
