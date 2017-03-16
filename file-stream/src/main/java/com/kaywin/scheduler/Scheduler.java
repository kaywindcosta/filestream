package com.kaywin.scheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kaywin.file.controller.model.CustomFileMetaData;
import com.kaywin.repository.FileRepository;

/*
 * Scheduler for scheduling jobs/tasks
 */
@Component
public class Scheduler {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	private static final Logger LOG = Logger.getLogger(Scheduler.class);

	@Value("${email.id}")
	private String emailid;

	@Value("${email.name}")
	private String name;

	@Autowired
	FileRepository repository;

	@Autowired
	private JavaMailSender javaMailService;

	@Scheduled(fixedRate = 10000 * 6 * 60)//scheduled every one hour
	public void sendMailToCustomers() {

		// last hour polled data
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1);
		Date date = cal.getTime();

		LOG.info("Scheduler Job running at " + dateFormat.format(date));

		List<CustomFileMetaData> files = repository.findByDatesBetween(date, new Date());
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(emailid);
			mailMessage.setSubject("Polled Records last hour " + new Date() + "and " + date);
			mailMessage.setText("Hello " + name + ", \n Last hour polled records are");

			for (CustomFileMetaData customFileMetaData : files) {
				mailMessage.setText(customFileMetaData.getFileName() + "  " + customFileMetaData.getPersonName() + "  "
						+ customFileMetaData.getDocumentDate());
			}
			LOG.info(mailMessage.getText());
			javaMailService.send(mailMessage);
		} catch (Exception e) {
			LOG.error("Email not send .... Connection error !! .Check connection settings in application.properties");
		}
		// for (CustomFileMetaData customFileMetaData : files) {
		// System.out.println(customFileMetaData.getFileName() + " " +
		// customFileMetaData.getPersonName() + " "
		// + customFileMetaData.getDocumentDate());
		// }

	}

	/*
	 * private final JavaMailSender javaMailSender;
	 * 
	 * @Autowired Scheduler(JavaMailSender javaMailSender) { this.javaMailSender
	 * = javaMailSender; }
	 * 
	 * 
	 * void SimpleMailMessagesend() { SimpleMailMessage mailMessage = new
	 * SimpleMailMessage(); mailMessage.setTo("test@gmail.com");
	 * mailMessage.setReplyTo("test@gmail.com");
	 * mailMessage.setFrom("someone@localhost"); mailMessage.setSubject("");
	 * mailMessage.setText(""); javaMailSender.send(mailMessage);
	 * 
	 * }
	 */
}
