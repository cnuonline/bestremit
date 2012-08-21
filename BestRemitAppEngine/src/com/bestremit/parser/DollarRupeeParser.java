
package com.bestremit.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bestremit.beans.RemitanceProviderBean;
import com.bestremit.beans.RemittanceCurrencyBean;
import com.bestremit.exceptions.BestRemitException;
import com.bestremit.servlet.BestRemitAppEngineServlet;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceFormatter;

/**
 * DollarRupee
 *
 * @author skoppa
 * @version $Id$
 */
public class DollarRupeeParser extends RupeeParser {
    
    private static final String CURRENCY_SYMBOL = "USD";
    private static final Logger log = Logger.getLogger(DollarRupeeParser.class.getName()); 
    
    public List<RemitanceProviderBean> parseDollar(String htmlContent) throws BestRemitException{
        
        List<RemitanceProviderBean> remitanceProviderBeans = new ArrayList<RemitanceProviderBean>();
        formatHtml(htmlContent, remitanceProviderBeans);
        return remitanceProviderBeans;
        
    }
    /**
     * @param htmlContent
     * @param remitanceProviderBean
     * @return
     * @throws BestRemitException
     */
    private SourceFormatter formatHtml(String htmlContent, List<RemitanceProviderBean> remitanceProviderBeans) throws BestRemitException{
       
        if (htmlContent.indexOf(':')==-1) htmlContent="file:"+htmlContent;
        MicrosoftConditionalCommentTagTypes.register();
        SourceFormatter sourceFormatter = null;
        try {
            
            PHPTagTypes.register();
            MasonTagTypes.register();
            Source source;           
           
            RemitanceProviderBean remitanceProviderBean = null;
            URL url = new URL(htmlContent);
           
            source = new Source(url);
           
           int amountRateCount = 0; 
          
          String table="table";  
           int tablePos=source.toString().indexOf(table);  

           Element tableElement=source.getEnclosingElement(tablePos,HTMLElementName.TABLE);  
           //System.out.println("tableElement: "+tableElement);

           
         //  System.out.println("table Segment filtered : "+ HTMLSanitiser.stripInvalidMarkup(tableSegment.toString()));
           for (Element rowElement : tableElement.getAllElements(HTMLElementName.TR)) { 
            int providerCount =0; //used for the retrieving the provider Bean.
            List<Element> rowElements = rowElement.getAllElements(HTMLElementName.TD);
            for (int i= 0 ; i<rowElements.size();i++) {  
                
                Element tdElement = rowElements.get(i);
                
                    if("tbl-hdr-1".equals(tdElement.getAttributeValue("class"))){
                       // TextExtractor textExtractor = new TextExtractor(tdElement.getContent());
                        remitanceProviderBean = new RemitanceProviderBean();
                        remitanceProviderBean.setRemittanceProviderName(tdElement.getContent().getTextExtractor().toString());
                        remitanceProviderBean.setCurrencySymbol(CURRENCY_SYMBOL);
                        remitanceProviderBeans.add(remitanceProviderBean);
                        
                    } else if("tbl-hdr-2".equals(tdElement.getAttributeValue("class"))){
                        amountRateCount++;
                        
                    }else {
                        RemittanceCurrencyBean remittanceCurrencyBean = new RemittanceCurrencyBean();
                        
                        remitanceProviderBean = remitanceProviderBeans.get(providerCount);
                        providerCount++;
                        
                        remittanceCurrencyBean.setAmount(tdElement.getContent().getTextExtractor().toString());
                        
                        if(i++ <rowElements.size()){
                            tdElement = rowElements.get(i);
                            remittanceCurrencyBean.setRate(tdElement.getContent().getTextExtractor().toString());
                        }else{
                            BestRemitException bestRemitException = new BestRemitException("Mismatch in the td elements: Lastelement "+tdElement+" total Remitance Providers: "
                                    +remitanceProviderBeans.size()+ " Provider Count:"+providerCount +" Provider Name : "+remitanceProviderBean.getRemittanceProviderName());
                            throw bestRemitException;
                        }
                       
                        remitanceProviderBean.getRemittanceCurrencies().add(remittanceCurrencyBean);
                    }                
                    
                
            }
          }
           log.log(Level.INFO, "Remitance Beans: "+ remitanceProviderBeans.toString());
          
            } catch (MalformedURLException e) {
               BestRemitException bestRemitException = new BestRemitException("DollarRupee: formatHtml MalformedURLException :",e);
               throw bestRemitException;
            } catch (IOException e) {
                BestRemitException bestRemitException = new BestRemitException("DollarRupee: formatHtml IOException :",e);
                throw bestRemitException;
            }catch (Exception e) {
                BestRemitException bestRemitException = new BestRemitException("DollarRupee: formatHtml Exception :",e);
                throw bestRemitException;
            }
        
            return sourceFormatter;
        
    }
    public static void main(String[] args) {
        DollarRupeeParser dollarRupee = new DollarRupeeParser();
        String htmlContent = "pagesource.html" ;
        try {
           // String htmlContent = "http://www.dollar2rupee.net";
            log.log(Level.INFO,dollarRupee.buildMessage(dollarRupee.parseDollar(htmlContent),"$"));
        } catch (BestRemitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private String buildMessage(List<RemitanceProviderBean> remitanceProviderBeans,String symbol) {
        StringBuffer message = new StringBuffer("<p> Here is the best remits available in the market <p>");
        String displaySymbol = null;
         for (RemitanceProviderBean remitanceProviderBean : remitanceProviderBeans) {
             RemittanceCurrencyBean remittanceCurrencyBean = remitanceProviderBean.getRemittanceCurrencies().get(0);
             displaySymbol =  BestRemitAppEngineServlet.displaySymbols.get(remitanceProviderBean.getCurrencySymbol());
             message.append(remitanceProviderBean.getRemittanceProviderName()).append(" Rs currency rate of ");
             message.append(remittanceCurrencyBean.getRate());
             message.append(" for a amount range ").append(displaySymbol).append("{")
                     .append(remittanceCurrencyBean.getAmount()).append("} ");
             message.append("</p>");
         }
        
         return message.toString();
     }
}
