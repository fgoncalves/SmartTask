package smartask.client.ui.sms;

/**
 * This class serves as a container for an SMS part.
 * @author Grupo 1
 */
public class SmsPart {

	public int total;
	public int part_num;
	public String id;
	public String body;
	public String from;
	
	public SmsPart(int total, int partNum, String id, String body, String from) {
		super();
		this.total = total;
		part_num = partNum;
		this.id = id;
		this.body = body;
		this.from = from;
	}
}
