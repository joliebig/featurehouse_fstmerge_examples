

package net.sf.freecol.client.gui.panel;

import java.text.DateFormat;

import javax.swing.JLabel;
import javax.xml.stream.XMLStreamException;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.HighScore;
import net.sf.freecol.common.model.Turn;

import org.w3c.dom.Element;

import net.miginfocom.swing.MigLayout;


public final class ReportHighScoresPanel extends ReportPanel {


    
    public ReportHighScoresPanel(Canvas parent) {
        super(parent, Messages.message("menuBar.game.highScores"));
        
        reportPanel.removeAll();

        Element report = getController().getHighScores();
        int number = report.getChildNodes().getLength();
        
        reportPanel.setLayout(new MigLayout("wrap 3, gapx 30", "[][][align right]", ""));

        for (int i = 0; i < number; i++) {
            Element element = (Element) report.getChildNodes().item(i);
            try {
                HighScore highScore = new HighScore(element);

                JLabel scoreValue = new JLabel(String.valueOf(highScore.getScore()));
                scoreValue.setFont(smallHeaderFont);
                reportPanel.add(scoreValue);
                
                String messageID = null;
                if (highScore.getIndependenceTurn() > 0) {
                    messageID = "report.highScores.president";
                } else {
                    messageID = "report.highScores.governor";
                }
                String country = highScore.getNewLandName();
                JLabel headline = new JLabel(Messages.message(messageID,
                                                              "%name%", highScore.getPlayerName(),
                                                              "%nation%", country));
                headline.setFont(smallHeaderFont);
                reportPanel.add(headline, "span, wrap 10");

                reportPanel.add(new JLabel(Messages.message("report.highScores.score")), "skip");
                reportPanel.add(new JLabel(String.valueOf(highScore.getScore())));

                reportPanel.add(new JLabel(Messages.message("report.highScores.difficulty")), "skip");
                reportPanel.add(new JLabel(Messages.message(highScore.getDifficulty())));

                reportPanel.add(new JLabel(Messages.message("report.highScores.independence")), "skip");
                int independenceTurn = highScore.getIndependenceTurn();
                String independence = independenceTurn > 0 ? Turn.toString(independenceTurn) :
                    Messages.message("no");
                reportPanel.add(new JLabel(independence));

                reportPanel.add(new JLabel(Messages.message("report.highScores.nation")), "skip");
                if (highScore.getIndependenceTurn() > 0) {
                    reportPanel.add(new JLabel(String.valueOf(highScore.getNationName())));
                } else {
                    reportPanel.add(new JLabel(String.valueOf(highScore.getOldNationName())));
                }

                reportPanel.add(new JLabel(Messages.message("report.highScores.nationType")), "skip");
                reportPanel.add(new JLabel(Messages.message(highScore.getNationTypeID() + ".name")));

                reportPanel.add(new JLabel(Messages.message("report.highScores.units")), "skip");
                reportPanel.add(new JLabel(String.valueOf(highScore.getUnits())));

                reportPanel.add(new JLabel(Messages.message("report.highScores.colonies")), "skip");
                reportPanel.add(new JLabel(String.valueOf(highScore.getColonies())));

                reportPanel.add(new JLabel(Messages.message("report.highScores.retired")), "skip");
                DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
                reportPanel.add(new JLabel(format.format(highScore.getDate())), "wrap 20");

            } catch (XMLStreamException e) {
                logger.warning(e.toString());
            }
        }

        reportPanel.doLayout();
    }

}
