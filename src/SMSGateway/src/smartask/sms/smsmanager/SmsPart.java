package smartask.sms.smsmanager;

/**
 * This class serves as a container, representing an SMS part. Whenever an SMS
 * with more than 160 characters is sent, it is divided into several of these
 * SMS parts.
 * 
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
