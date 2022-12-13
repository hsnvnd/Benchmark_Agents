package Benchmark_Agents.Skew_normal_Distribution;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.issue.Issue;
import genius.core.parties.NegotiationInfo;
import genius.core.timeline.TimeLineInfo;
import genius.core.boaframework.SortedOutcomeSpace;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import MOSALASI.test11;
import agents.org.apache.commons.math.MathException;
import agents.org.apache.commons.math.special.Erf;
import genius.core.AgentID;
import genius.core.BidHistory;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.actions.EndNegotiationWithAnOffer;
import genius.core.actions.Offer;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.xml.SimpleElement;
//this agent has a e=0.1 concession strategy  
@SuppressWarnings("serial")
public class Half_Left_N025_neg extends AbstractNegotiationParty {
	Scanner input = new Scanner (System.in);
////////////
	int counter =1;
	private SortedOutcomeSpace outcomeSpace;
    private TimeLineInfo TimeLineInfo = null;
    private AbstractUtilitySpace utilSpace = null;
    private BidHistory OtherAgentsBidHistory1;
    double lEvalValue=0.0;
    double norm=0.0;
    List<Issue> issues =null;
    private Map <String, String> allvalues = new LinkedHashMap<String, String>();
    private Map <String, String> allnormalvalues = new LinkedHashMap<String, String>();
    private static Bid lastReceivedOffer;
    
    public Half_Left_N025_neg() {
    	
      //  this.OtherAgentsBidHistory1 = new BidHistory();
        this.lastReceivedOffer = null;
}    
@Override
	public void init(NegotiationInfo info)
	{
		super.init(info);
		this.utilSpace = info.getUtilitySpace(); // read utility space		
	/////////////////////////choose utility function//////////////////////////////////	
		skew_normal();	
	////////////////////////////////////////////////////////////////////////////
		utilSpace = info.getUtilitySpace();// read utility space
		issues = utilSpace.getDomain().getIssues();
		outcomeSpace = new SortedOutcomeSpace(utilSpace);
		}
/**************************************************************************************************/
    public void skew_normal() {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> alist=new ArrayList<String>();
			SimpleElement un = utilSpace.toXML();
	    	un.getAttributes();
	    	un.getChildElementsAsList();
	    	String myfile=utilSpace.getFileName();
	    	un.saveToFile(myfile);	
///////////////////////////////////////////////////////////////////////////
	    	try {
	    		org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance()
				            .newDocumentBuilder().parse(new InputSource(myfile));	
	/** This part is responsible for changing the "evaluation" of values, based upon their "value" by calling a utility function */	
	    		NodeList flowList = doc.getElementsByTagName("issue");
	    		for (int i = 0; i < flowList.getLength(); i++) {
	    			//correct System.out.println("\n For issue index "+i+ " we have these values:");
	    		    NodeList childList = flowList.item(i).getChildNodes();
	    		    for (int l = 0; l < childList.getLength(); l++) {
	    		    	Node childNod = childList.item(l);
	    				if ("item".equals(childNod.getNodeName())) {
	    					String digits = null;
	    					Node node = childList.item(l);
	    					Element eElement = (Element) node;	
	    					String val = eElement.getAttribute("value");
	    					///
	    					digits = val;
    						String digi=digits.replaceAll("[^0-9.]", "");
    						digits = (digi.isEmpty()) ? "0.0" : digi;
	    					///
	    					alist.add(digits);
	    					//correct System.out.println(val);
				        }}

//////////////////////////////////////////////////////////////////////////////////////////////////		
	    		  //first value
					String peak = alist.get(0);
					float w = (float) 0.01;
					float shape = -10;
    				alist.clear();
//////////////////////////////////////////////////////////
	    			for (int j = 0; j < childList.getLength(); j++) {
	    				Node childNode = childList.item(j);
	    				if ("item".equals(childNode.getNodeName())) {
	    					String digits = null;
	    					Node node = childList.item(j);
	    					Element eElement = (Element) node;	
	    					String val = eElement.getAttribute("value");
	    					digits = val;
    						String digi=digits.replaceAll("[^0-9.]", "");
    						digits = (digi.isEmpty()) ? "0.0" : digi;
	/**************************************** m, min1,min2,peak ***********************************/
	    					double result= skew_normal_distribution(digits, w ,
				            peak, shape);
	    					String result2 = String.valueOf(result);
	    					Node Evaluation = childList.item(j).getAttributes().getNamedItem("evaluation");
	    					Evaluation.setTextContent(result2);
	    						/////////////
	    					allvalues.put(digits,result2);
	    					String eoll = System.getProperty("line.separator");
	    					try (Writer writer = new FileWriter("skew_normal_values&Utility.csv")) {
	    						for (Map.Entry<String, String> entry : allvalues.entrySet()) {
	    							writer.append(entry.getKey())
							         .append(',')
							         .append(entry.getValue())
							         .append(eoll);
							  }
							}
	    					catch (IOException ex) {
	    						ex.printStackTrace(System.err);
							}
	    					}}
////////////////////////////////////////////////Normalize and save in a new csv/////////////////////////////////		
	    		try (Stream<String> stream = Files.lines(Paths.get("skew_normal_values&Utility.csv"))) {
	    			DoubleSummaryStatistics statistics = stream
	    		           .map(s -> s.split(",")[1])
	    		           .mapToDouble(Double::valueOf)
	    		           .summaryStatistics();
	    		 //   System.out.println("Lowest:: " + statistics.getMin());
	    			norm=statistics.getMax();
	    		  //correct  System.out.println("Highest:: " + statistics.getMax());
	    		    } catch (IOException e) {
	    		        e.printStackTrace();
	    		    }
///////////////////////////////////////////////////////////////////////////////////////////
	    			for (int e = 0; e < childList.getLength(); e++) {
	    				Node childNode = childList.item(e);
	    				if ("item".equals(childNode.getNodeName())) {
	    					String digits = null;
	    					Node node = childList.item(e);
	    					Element eElement = (Element) node;	
	    					String eval = eElement.getAttribute("evaluation");

	    					String val = eElement.getAttribute("value");
	    					digits = val;
    						String digi=digits.replaceAll("[^0-9.]", "");
    						String digitss = (digi.isEmpty()) ? "0.0" : digi;
	    					
/**************************************************************************/
	    					//String result2 = String.valueOf(result);
	    					double normali=Double.parseDouble(eval);
	    					Node Evaluation = childList.item(e).getAttributes().getNamedItem("evaluation");
	    					normali=normali/norm;
	    					String resu = String.valueOf(normali);
	    					Evaluation.setTextContent(resu);
////////////////////////////////////////////////////////////////////////////////////////////   					
	    					allnormalvalues.put(digitss,resu);
	    					String eol = System.getProperty("line.separator");
	    					try (Writer writer = new FileWriter("skew_values&Utility_normalized.csv")) {
	    						for (Map.Entry<String, String> entry : allnormalvalues.entrySet()) {
	    							writer.append(entry.getKey())
	    							.append(',')
	    							.append(entry.getValue())
	    							.append(eol);
						  }
						}
    					catch (IOException ex) {
    						ex.printStackTrace(System.err);
						}}}
//////////////////////////////////////////////////////////////////////////////////
	    				// 4- Save the result to a new XML doc
	    		Transformer xformer = TransformerFactory.newInstance().newTransformer();
	    		xformer.transform(new DOMSource(doc), new StreamResult(new File(myfile)));}}
				/////////////
			
	    	catch (ParserConfigurationException | SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
			catch (IOException e1) {
			// TODO Auto-generated catch block
				e1.printStackTrace();
		}}
    	
/***********************************************************************************/
public boolean check_ascending (ArrayList<String> data) {
	for (int i = 0; i < data.size()-1; i++) {
	    if (Double.parseDouble(data.get(i)) > Double.parseDouble(data.get(i+1))) {
	        return false;
	    }
	}
	return true;
}   
/**********************************************************************************/
/**
public int countInRange(ArrayList<String> arr, int n, int x, int y) 
{ 
    // initialize result 
    int count = 0; 
    
    if(check_ascending(arr)) {
    	
    	for (int i = 0; i < n; i++) { 
        // check if element is in range 
    		if ((Double.parseDouble(arr.get(i)) > x) && (Double.parseDouble(arr.get(i)) <= y))
    			count++; 
    }}
    else if(!(check_ascending(arr))){
    	for (int i = 0; i < n; i++) { 
            // check if element is in range 
        	if ((Double.parseDouble(arr.get(i))) < x && (Double.parseDouble(arr.get(i)) >= y))
        		count++; 
        }
    }
   
    return count; 
} 
**/
/**********************************************************************************/	
	public Bid pickBidOfUtility(double utility) {
		return outcomeSpace.getBidNearUtility(utility).getBid();}
/**********************************************************************************/	
	public double non_conceder(double t,double Pmin) {	
		return Pmin + (1 - Pmin) * t;
		}
/********************************************************************************/
	public Bid makeBid() {
		Bid bid=null;
		double time = timeline.getTime();
		double utilityGoal = 0;
/****************************************************/
		//important	
		utilityGoal = non_conceder(time, 0.25);		
//////////////////////////////////////////////////////	
		bid = pickBidOfUtility(utilityGoal);
		//correct System.out.println("**************getFileName is***********"+utilSpace.getFileName());
		double bid_utility = getUtility(bid);
		String bid_string = String.valueOf(bid);
		String bidutil = String.valueOf(bid_utility);	
		String bid_digits = bid_string.replaceAll("[^0-9.]", "");
		String concat_b = bidutil + "," + bid_digits;
		String eol = System.getProperty("line.separator");
		/**
		try (Writer writer = new FileWriter("all_bids_I_offered.csv",true)) {
			writer.append(concat_b).append(eol);
			writer.flush();
			writer.close();
		} catch (IOException ex) {
		  ex.printStackTrace(System.err);
		}**/
///////////////////////////////////////////////////////
		//Not necessary
		//System.out.println("This is round "+counter);
		//System.out.println("current Target is "+utilityGoal);
		//System.out.println("current Bid is "+bid);
		//System.out.println("Bid's utility is "+bid_utility);
		
		String co = String.valueOf(counter);
		String ug = String.valueOf(utilityGoal);		
		String concat_values = co + "," + ug + ","+ bid_digits +","+ bidutil;
		System.out.println(concat_values);
		/**
		try {
			FileWriter pw = new FileWriter("details.csv",true);
			pw.append(concat_values).append(eol);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} **/
		counter++;
///////////////////////////////////////////////////////	
		
		return bid;}
/****************************************************************************************************/  
	public double skew_normal_distribution(String m,float scale, String loc,float shape) {
		// TODO Auto-generated method stub
		double x =Double.parseDouble(m); 
		double peak =Double.parseDouble(loc);
		////////////////////
		double answer = 0;
		try {
			 answer = (1/scale* Math.sqrt(2*Math.PI)) * (Math.exp(-Math.pow(x-peak,2)/2*Math.pow(scale,2))) *
					  (Math.PI * ( Erf.erf(((Math.sqrt(2)*shape*x)-(Math.sqrt(2)*shape*peak))/2*scale) +1) /Math.sqrt(2))							   
							   ;}
		catch (MathException e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		} 
		return answer;	}
/*****************************************************************************************************/   
@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		Action action1;
		Action action = null;
	  	try {
	  		if (this.lastReceivedOffer == null) {
	  			action= new Offer(getPartyId(), makeBid());
	  		}
	  		else {
	  			//return new Offer(getPartyId(), makeBid());
	           
	  			action1 = new Offer(getPartyId(),makeBid());
	  			Bid myBid = ((Offer) action1).getBid();
	        
	        /////
	  			action = ((getUtility(this.lastReceivedOffer)) >= getUtility(myBid)) ? (Action)new Accept(this.getPartyId(),
	  					this.lastReceivedOffer):new Offer(getPartyId(), makeBid());
	  				//action= (Action)new Accept(this.getPartyId(), this.lastReceivedOffer);
	  			}
	        
	
	        ///
	  		}
	    catch (Exception e) {
	    	return new Offer(getPartyId(), makeBid());
	   }
		return action;}
	/****************************************************************************************************/	
	public void receiveMessage(AgentID sender, Action action) {
		 super.receiveMessage(sender, action);
	      // Here you hear other parties' messages
	      if (action instanceof Offer) {
	    	  Bid bid = ((Offer) action).getBid();
	    	  Half_Left_N025_neg.lastReceivedOffer = ((Offer)action).getBid();
	          try {
	        	  BidDetails opponentBid = new BidDetails(bid,
	        			  utilSpace.getUtility(bid), TimeLineInfo.getTime());
	              OtherAgentsBidHistory1.add(opponentBid);
	              
	        }catch (Exception e) {
	        	  EndNegotiationWithAnOffer end = new EndNegotiationWithAnOffer(
	                                      this.getPartyId(),  makeBid());
	        	  }}
	      
	     
	    //  
	}
/****************************************************************************************************/	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		 return "Singly_Peaked Bidding Agent";}
}
	
	