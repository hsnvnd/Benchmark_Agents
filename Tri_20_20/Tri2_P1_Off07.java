package Benchmark_Agents.Tri_20_20;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
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
public class Tri2_P1_Off07 extends AbstractNegotiationParty {
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
	
    private static Bid lastReceivedOffer;
    
    public Tri2_P1_Off07() {
    	
      //  this.OtherAgentsBidHistory1 = new BidHistory();
        this.lastReceivedOffer = null;
}    
    
@Override
	public void init(NegotiationInfo info)
	{
		super.init(info);
		this.utilSpace = info.getUtilitySpace(); // read utility space		
	/////////////////////////choose utility function//////////////////////////////////	
		triangular();	
	////////////////////////////////////////////////////////////////////////////
		utilSpace = info.getUtilitySpace();// read utility space
		issues = utilSpace.getDomain().getIssues();
		outcomeSpace = new SortedOutcomeSpace(utilSpace);
		}
/**************************************************************************************************/

    public void triangular() {
    	try {
    		ArrayList<String> alist=new ArrayList<String>();
    		SimpleElement un = utilSpace.toXML();
    		un.getAttributes();
    		un.getChildElementsAsList();
    		String myfile=utilSpace.getFileName();
    		un.saveToFile(myfile);	
///////////////////////////////////////////////////
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
//////////////////////////////////////////////////////////////
    				float truemin1 = 0;
    				float truemin2 = 0;
    				/// set left, right, peak, pow, and height
    				float left =20;
    				float right = 20;		
    				 //since the peak point is median, we have used the following code
	    		    String median =  alist.get((alist.size()-1)/2);
	    		
	    		    String peak = median;
					float pow =1;
					float height = 1;
    				// find first element 
    		        String first = alist.get(0); 
    		        // find last element 
    		        String last = alist.get(alist.size() - 1); 
    		        // peak's index
    		        int peak_index = alist.indexOf(peak);
    		        // count number of elements from the start till peak
    		        int left_element_count = countInRange (alist,alist.size(),Double.parseDouble(first),Double.parseDouble(peak));
       		     // count number of elements from peak till the end
       		        int right_element_count = countInRange (alist,alist.size(),Double.parseDouble(peak),Double.parseDouble(last));
    		        System.out.println ("left_element_count count is" +left_element_count);
					System.out.println ("right_element_count count is" +right_element_count);
    		      //
					if (peak_index==0)
					{
						truemin1 = Float.parseFloat(peak) - 1;
					}
					else if (peak_index == (alist.size() - 1)){
						truemin2 = Float.parseFloat(peak) + 1; 
						}
					else
					{
						double minn1 = Math.ceil(((left_element_count*left)/100));
						System.out.println("min1 is "+ minn1 +" element");
						double minn2 = Math.ceil(((right_element_count*right)/100));
						System.out.println("min2 is "+ minn2 + " element");
						truemin1 = ((minn1==0) ? (Float.parseFloat(alist.get((int)(peak_index-1)))) :
						 (Float.parseFloat(alist.get((int) (peak_index-minn1)))));
						truemin2 = (minn2==0 ? (Float.parseFloat(alist.get((int)peak_index+1))) : 
						(Float.parseFloat(alist.get((int) (peak_index+minn2)))));
					}
					/////
    				alist.clear();
    				

    				//correct System.out.println("tri_arguments length is "+ tri_arguments.length);
////////////////////////////////////////////////////////////////////////////////////
    				for (int j = 0; j < childList.getLength(); j++) {
    					Node childNode = childList.item(j);
    					if ("item".equals(childNode.getNodeName())) {
    						Node node = childList.item(j);
    						String digits = null;
	    					Element eElement = (Element) node;	
	    					String val = eElement.getAttribute("value");
	    					///
	    					digits = val;
    						String digi=digits.replaceAll("[^0-9.]", "");
    						digits = (digi.isEmpty()) ? "0.0" : digi;
/**************************************** m, min1,min2,peak ***********************************/
    						
    						///////////
    						
    						double result= create_General_Triangular(digits, peak, pow, height, truemin1, truemin2);
    						
    						String result2 = String.valueOf(result);
    						Node Evaluation = childList.item(j).getAttributes().getNamedItem("evaluation");
    						Evaluation.setTextContent(result2);
    						
    						/////////////
    						allvalues.put(digits,result2);
    						String eoll = System.getProperty("line.separator");
    						try (Writer writer = new FileWriter("triangular_values&Utility.csv")) {
    							for (Map.Entry<String, String> entry : allvalues.entrySet()) {
    								writer.append(entry.getKey())
						          .append(',')
						          .append(entry.getValue())
						          .append(eoll);
						  }}
    						catch (IOException ex) {
    							ex.printStackTrace(System.err);
						}}}}
    				// 4- Save the result to a new XML doc
    				Transformer xformer = TransformerFactory.newInstance().newTransformer();
    				xformer.transform(new DOMSource(doc), new StreamResult(new File(myfile)));
    				
////////////////////////////////////////////////////////////////////////		
		} catch (ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
}
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
public int countInRange(ArrayList<String> arr, int n, double d, double e) 
{ 
    // initialize result 
    int count = 0; 
    
    if(check_ascending(arr)) {
    	
    	for (int i = 0; i < n; i++) { 
        // check if element is in range 
    		if ((Double.parseDouble(arr.get(i)) > d) && (Double.parseDouble(arr.get(i)) <= e))
    			count++; 
    }}
    else if(!(check_ascending(arr))){
    	for (int i = 0; i < n; i++) { 
            // check if element is in range 
        	if ((Double.parseDouble(arr.get(i))) < d && (Double.parseDouble(arr.get(i)) >= e))
        		count++; 
        }
    }
   
    return count; 
} 
/**********************************************************************************/	
	public Bid pickBidOfUtility(double utility) {
		return outcomeSpace.getBidNearUtility(utility).getBid();}
/**********************************************************************************/	
	public double conceder(double t,double Pmax,double E) {
		return Pmax * (1 - Math.pow(t, 1/E));
		}

/********************************************************************************/
	public Bid makeBid() {
		Bid bid=null;
		double time = timeline.getTime();
		double utilityGoal = 0;
/****************************************************/
		//important
		utilityGoal = conceder(time, 0.7 ,1);	
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
/**
 * @param last 
 * @param first ****************************************************************************************************/	
   public double create_General_Triangular(String m, String peak, float pow,
		   float height, float truemin1, float truemin2) {
	   double x =Double.parseDouble(m); 
	   double Peak =Double.parseDouble(peak);
	   //
			if (x<truemin1) return 0;
			else
				if(x<Peak) return  height*(Math.pow((x-truemin1)/(Peak-truemin1),pow));
				else 
					if(x<truemin2) return height*(Math.pow((1-(x-Peak)/(truemin2 -  Peak)),pow));
					else return 0;	}

   //////////////////////
  
	
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
	    	  Tri2_P1_Off07.lastReceivedOffer = ((Offer)action).getBid();
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
	
	

/********************************************************************************/

	