<?php
	//This php will handle the creation and sending of email notifications for friend requests, group requests, event requests, and event updates

	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	@require_once '/../../bin/php/php5.5.12/pear/mail.php'; 
	include_once '/../../includes/stmt_connect.inc.php'; 
		
		
	if($argv[1] === "F")
	{
		if($argc != 4)
		{	
			echo "Usage: php mail.php F sender receiver";
			exit();
		}
		echo "Friend request started\n";
		echo "1\n";
		//store the supplied arguments
		$sender = $argv[2];
		$receiver = $argv[3];
				
		//now check to see if the receiver has email notifications enabled
		
		echo "1\n";
		
		$stmt = $mysqli->prepare("SELECT emailFriendReq FROM users_settings WHERE email = ?");
		$stmt->bind_param('s', $receiver);
		$stmt->execute();
		$stmt->bind_result($emailFriendReq);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;
		
		echo "2\n";

		//a user setting file was found
		if($row_cnt > 0)
		{
			echo "3\n";
			$stmt->fetch();	
			if($emailFriendReq == 0)
			{
				echo "Receiver email has friend request email notifications turned off... no email will be sent.";
				exit();
			}
		}
		//no settings file was found for receiver email
		else
		{
			echo "4\n";
			echo "Receiver email supplied is not a user... no email will be sent.";
			exit();
		}
		
		//get additional information about users to form a better email body
		echo "5\n";
		//get first, last name of sender
		$stmt = $mysqli->prepare("SELECT first,last FROM users WHERE email = ?");
		$stmt->bind_param('s', $sender);
		$stmt->execute();
		$stmt->bind_result($senderfirst, $senderlast);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no sender user was found
		if($row_cnt <= 0)
		{
			echo "Sender email is not a user... no email will be sent.";
			exit();
		}
		$stmt->fetch();
		
		//get first, last name of receiver
		$stmt = $mysqli->prepare("SELECT first,last FROM users WHERE email = ?");
		$stmt->bind_param('s', $receiver);
		$stmt->execute();
		$stmt->bind_result($receiverfirst, $receiverlast);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no receiver user was found
		if($row_cnt <= 0)
		{
			echo "Receiver email is not a user... no email will be sent.";
			exit();
		}
		$stmt->fetch();
		
		//set up email headers
		$from = "Grouple <grouple-noreply@grouple.com>";		
		$to = $receiverfirst.' '.$receiverlast.' <'.$receiver.'>';
		$subject = "{$senderfirst} {$senderlast} wants to be your friend!";
		$headers = array ('From' => $from,   'To' => $to,   'Subject' => $subject);
		
		//Note: signaturerandomizer used to avoid gmail trimming
		$signatureRandomizer = str_shuffle("++++++++--------");
		
		//set up email body
		$body = "Hi {$receiverfirst},\n\nYou have a new friend request from {$senderfirst} {$senderlast} ({$sender}).  Please check your Grouple application now to accept or decline the friend invite.\n\nNote:  To disable these email notifications, simply adjust your Settings within the Grouple application.\nAs always, thanks for using Grouple!\n\n-Grouple Support\n\n{$signatureRandomizer}";
		
		//send the email (port, username, and password are stored in external includes)
		$smtp = @Mail::factory('smtp',  
		array ('host' => $host,  
		'port' => $port,  
		'auth' => true,   
		'username' => $username,  
		'password' => $password));
		$mail = @$smtp->send($to, $headers, $body);
		if (@PEAR::isError($mail)) 
		{   
			echo "Unable to process email.";		
		} 
		else 
		{ 
			echo "Successfully sent a friend request email notification";	
		}
	}
	else if($argv[1] === 'G')
	{
		if($argc != 5)
		{	
			echo "Usage: php mail.php G sender receiver gid";
			exit();
		}
		echo "Group request started\n";
		
		//store the supplied arguments
		$sender = $argv[2];
		$receiver = $argv[3];
		$gid = $argv[4];
		
		//first check to make sure you are not inviting yourself
		if($receiver === $sender)
		{
			exit();
		}
		
		//check to see if the receiver has email notifications enabled
		
		$stmt = $mysqli->prepare("SELECT emailGroupReq FROM users_settings WHERE email = ?");
		$stmt->bind_param('s', $receiver);
		$stmt->execute();
		$stmt->bind_result($emailGroupReq);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//a user setting file was found
		if($row_cnt > 0)
		{
			$stmt->fetch();	
			if($emailGroupReq == 0)
			{
				echo "Receiver email has group request email notifications turned off... no email will be sent.";
				exit();
			}
		}
		//no settings file was found for receiver email
		else
		{
			echo "Receiver email supplied is not a user... no email will be sent.";
			exit();
		}
		
		//get additional information about users to form a better email body
		
		//get first, last name of sender
		$stmt = $mysqli->prepare("SELECT first,last FROM users WHERE email = ?");
		$stmt->bind_param('s', $sender);
		$stmt->execute();
		$stmt->bind_result($senderfirst, $senderlast);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no sender user was found
		if($row_cnt <= 0)
		{
			echo "Sender email is not a user... no email will be sent.";
			exit();
		}
		$stmt->fetch();
		
		//get first, last name of receiver
		$stmt = $mysqli->prepare("SELECT first,last FROM users WHERE email = ?");
		$stmt->bind_param('s', $receiver);
		$stmt->execute();
		$stmt->bind_result($receiverfirst, $receiverlast);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no receiver user was found
		if($row_cnt <= 0)
		{
			echo "Receiver email is not a user... no email will be sent.";
			exit();
		}
		$stmt->fetch();
		
		//get g_name of gid
		$stmt = $mysqli->prepare("SELECT g_name FROM groups WHERE g_id = ?");
		$stmt->bind_param('s', $gid);
		$stmt->execute();
		$stmt->bind_result($groupname);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no group was found
		if($row_cnt <= 0)
		{
			echo "gid is not a group... no email will be sent.";
			exit();
		}
		$stmt->fetch();
				
		//set up email headers
		$from = "Grouple <grouple-noreply@grouple.com>";		
		$to = $receiverfirst.' '.$receiverlast.' <'.$receiver.'>';
		$subject = "{$senderfirst} {$senderlast} has invited you to a Group!";
		$headers = array ('From' => $from,   'To' => $to,   'Subject' => $subject);
		
		//Note: signaturerandomizer used to avoid gmail trimming
		$signatureRandomizer = str_shuffle("++++++++--------");
		
		//set up email body
		$body = "Hi {$receiverfirst},\n\nYou have a new group request from {$senderfirst} {$senderlast} ({$sender}) to join the group:\n\n\"{$groupname}\"\n\nPlease check your Grouple application now to accept or decline the group invite.\n\nNote:  To disable these email notifications, simply adjust your Settings within the Grouple application.\nAs always, thanks for using Grouple!\n\n-Grouple Support\n\n{$signatureRandomizer}";
		
		//send the email (port, username, and password are stored in external includes)
		$smtp = @Mail::factory('smtp',  
		array ('host' => $host,  
		'port' => $port,  
		'auth' => true,   
		'username' => $username,  
		'password' => $password));
		$mail = @$smtp->send($to, $headers, $body);
		if (@PEAR::isError($mail)) 
		{   
			echo "Unable to process email.";		
		} 
		else 
		{ 
			echo "Successfully sent a group request email notification";	
		}
	}
	else if($argv[1] === 'E')
	{
		if($argc != 5)
		{	
			echo "Usage: php mail.php E sender receiver eid";
			exit();
		}
		echo "Event request started\n";
		
		//store the supplied arguments
		$sender = $argv[2];
		$receiver = $argv[3];
		$eid = $argv[4];
		
		//first check to make sure you are not inviting yourself
		if($receiver === $sender)
		{
			exit();
		}
		
		//now check to see if the receiver has email notifications enabled
		
		$stmt = $mysqli->prepare("SELECT emailEventReq FROM users_settings WHERE email = ?");
		$stmt->bind_param('s', $receiver);
		$stmt->execute();
		$stmt->bind_result($emailEventReq);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//a user setting file was found
		if($row_cnt > 0)
		{
			$stmt->fetch();	
			if($emailEventReq == 0)
			{
				echo "Receiver email has event request email notifications turned off... no email will be sent.";
				exit();
			}
		}
		//no settings file was found for receiver email
		else
		{
			echo "Receiver email supplied is not a user... no email will be sent.";
			exit();
		}
		
		//get additional information about users to form a better email body
		
		//get first, last name of sender
		$stmt = $mysqli->prepare("SELECT first,last FROM users WHERE email = ?");
		$stmt->bind_param('s', $sender);
		$stmt->execute();
		$stmt->bind_result($senderfirst, $senderlast);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no sender user was found
		if($row_cnt <= 0)
		{
			echo "Sender email is not a user... no email will be sent.";
			exit();
		}
		$stmt->fetch();
		
		//get first, last name of receiver
		$stmt = $mysqli->prepare("SELECT first,last FROM users WHERE email = ?");
		$stmt->bind_param('s', $receiver);
		$stmt->execute();
		$stmt->bind_result($receiverfirst, $receiverlast);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no receiver user was found
		if($row_cnt <= 0)
		{
			echo "Receiver email is not a user... no email will be sent.";
			exit();
		}
		$stmt->fetch();
		
		//get e_name of eid
		$stmt = $mysqli->prepare("SELECT e_name FROM events WHERE e_id = ?");
		$stmt->bind_param('s', $eid);
		$stmt->execute();
		$stmt->bind_result($eventname);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no event was found
		if($row_cnt <= 0)
		{
			echo "eid is not an event... no email will be sent.";
			exit();
		}
		$stmt->fetch();
		
		//set up email headers
		$from = "Grouple <grouple-noreply@grouple.com>";		
		$to = $receiverfirst.' '.$receiverlast.' <'.$receiver.'>';
		$subject = "{$senderfirst} {$senderlast} has invited you to an Event!";
		$headers = array ('From' => $from,   'To' => $to,   'Subject' => $subject);
		
		//Note: signaturerandomizer used to avoid gmail trimming
		$signatureRandomizer = str_shuffle("++++++++--------");
		
		//set up email body
		$body = "Hi {$receiverfirst},\n\nYou have a new event request from {$senderfirst} {$senderlast} ({$sender}) to join the event:\n\n\"{$eventname}\"\n\nPlease check your Grouple application now to accept or decline the event invite.
		\n\nNote:  To disable these email notifications, simply adjust your Settings within the Grouple application.\nAs always, thanks for using Grouple!\n\n-Grouple Support\n\n{$signatureRandomizer}";
		
		//send the email (port, username, and password are stored in external includes)
		$smtp = @Mail::factory('smtp',  
		array ('host' => $host,  
		'port' => $port,  
		'auth' => true,   
		'username' => $username,  
		'password' => $password));
		$mail = @$smtp->send($to, $headers, $body);
		if (@PEAR::isError($mail)) 
		{   
			echo "Unable to process email.";		
		} 
		else 
		{ 
			echo "Successfully sent a event request email notification";	
		}
	}
	else if($argv[1] === 'EU')
	{
		if($argc != 11)
		{	
			echo "Usage: php mail.php EU sender eid old_e_name old_start_date old_end_date old_category old_about old_location old_recurring_type";
			exit();
		}
		echo "Event Update request started\n";
		
		//store the supplied arguments
		$sender = str_replace('_', ' ', $argv[2]);
		$eid = str_replace('_', ' ', $argv[3]);
		$old_e_name = str_replace('_', ' ', $argv[4]);
		$old_start_date = str_replace('_', ' ', $argv[5]);
		$old_end_date = str_replace('_', ' ', $argv[6]);
		$old_category = str_replace('_', ' ', $argv[7]);
		$old_about = str_replace('_', ' ', $argv[8]);
		$old_location = str_replace('_', ' ', $argv[9]);
		$old_recurring_type = str_replace('_', ' ', $argv[10]);
		
		//get additional information to form a better email body
		
		//get first, last name of the person who did the update
		$stmt = $mysqli->prepare("SELECT first,last FROM users WHERE email = ?");
		$stmt->bind_param('s', $sender);
		$stmt->execute();
		$stmt->bind_result($senderfirst, $senderlast);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no sender user was found
		if($row_cnt <= 0)
		{
			echo "Sender email is not a user... no emails will be sent.";
			exit();
		}
		$stmt->fetch();
		
		//get new information about event of eid
		$stmt = $mysqli->prepare("SELECT e_name, about, start_date, end_date, category, location, recurring_type FROM events WHERE e_id = ?");
		$stmt->bind_param('s', $eid);
		$stmt->execute();
		$stmt->bind_result($new_e_name, $new_about, $new_start_date, $new_end_date, $new_category, $new_location, $new_recurring_type);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;

		//no event was found
		if($row_cnt <= 0)
		{
			echo "eid is not an event... no email will be sent.";
			exit();
		}
		$stmt->fetch();
				
		//get a list of all participants to this event to determine list of receivers
		$stmt2 = $mysqli->prepare("SELECT u.email, u.first, u.last FROM users u JOIN e_members em ON em.email = u.email WHERE em.e_id = ? AND em.rec_date IS NOT NULL ORDER BY u.last");
		$stmt2->bind_param('i', $eid);
		$stmt2->execute();
		$stmt2->bind_result($receiver,$receiverfirst,$receiverlast);
		$stmt2->store_result();
		$row_cnt = $stmt2->num_rows;
		
		if($row_cnt > 0)
		{	
			echo "number of participants found for this event is: {$row_cnt}\n";
			while($stmt2->fetch())
			{
				$send = "true";
				echo "{$receiver}\n";
				//first check to see if the receiver has email notifications enabled
		
				$stmt = $mysqli->prepare("SELECT emailEventUpcoming FROM users_settings WHERE email = ?");
				$stmt->bind_param('s', $receiver);
				$stmt->execute();
				$stmt->bind_result($emailEventUpcoming);
				$stmt->store_result();
				$row_cnt = $stmt->num_rows;

				//a user setting file was found
				if($row_cnt > 0)
				{
					$stmt->fetch();
					if($emailEventUpcoming == 0)
					{
						echo "Receiver email has event updates email notifications turned off... no email will be sent.";
						$send = "false";
					}
				}
				//no settings file was found for receiver email
				else
				{
					echo "Receiver email supplied is not a user... no email will be sent.";
				}
				
				if($send === "true")
				{
					//set up email headers
					$from = "Grouple <grouple-noreply@grouple.com>";		
					$to = $receiverfirst.' '.$receiverlast.' <'.$receiver.'>';
				
					//check to see if sender is currently receiver. if so, we can change the body to make it a self-reporting confirmation email.
					if($sender === $receiver)
					{
						$subject = "You have successfully updated the info to one of your events!";
					}
					else
					{
						$subject = "{$senderfirst} {$senderlast} has updated the info to one of your events!";
					}
					$headers = array ('From' => $from,   'To' => $to,   'Subject' => $subject, 'Content-type' => "text/html");
					
					//Note: signaturerandomizer used to avoid gmail trimming
					$signatureRandomizer = str_shuffle("++++++++--------");
					
					//set up email body
					if($sender === $receiver)
					{
						$body = "Hi {$receiverfirst},<br>\n\nThis is confirming your changes that you recently made to the info of one of the events you are an Admin of.<br>\nThe event \"{$old_e_name}\" now has this information:<br><br>\n\n";
					}
					else
					{
						$body = "Hi {$receiverfirst},<br>\n\n{$senderfirst} {$senderlast} ({$sender}) has recently updated the information for one of the events you are a participant of.<br>\nThe event \"{$old_e_name}\" now has this information:<br><br>\n\n";
					}
					
					//determine which info changed and add only those lines to body
					if($old_e_name !== $new_e_name)
					{
						$body .= "	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-\"Event Name\" has been changed from \"{$old_e_name}\" to: <b>\"{$new_e_name}\"</b>\n.<br>";
					}
					if($old_about !== $new_about)
					{
						$body .= "	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-\"Event Description\" has been changed from \"{$old_about}\" to: <b>\"{$new_about}\"</b>\n.<br>";
					}
					if($old_start_date !== $new_start_date)
					{
						$body .= "	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-\"Start Date\" has been changed from \"{$old_start_date}\" to: <b>\"{$new_start_date}\"</b>\n.<br>";
					}
					if($old_end_date !== $new_end_date)
					{
						$body .= "	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-\"End Date\" has been changed from \"{$old_end_date}\" to: <b>\"{$new_end_date}\"</b>\n.<br>";
					}
					if($old_category !== $new_category)
					{
						$body .= "	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-\"Category\" has been changed from \"{$old_category}\" to: <b>\"{$new_category}\"</b>\n.<br>";
					}
					if($old_location !== $new_location)
					{
						$body .= "	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-\"Location\" has been changed from \"{$old_location}\" to: <b>\"{$new_location}\"</b>\n.<br>";
					}
					if($old_recurring_type !== $new_recurring_type)
					{
						if($old_recurring_type === "O")
						{
							$ort = "One-time Event";
						}
						else if($old_recurring_type === "A")
						{
							$ort = "Annually";
						}
						else if($old_recurring_type === "M")
						{
							$ort = "Monthly";
						}
						else if($old_recurring_type === "W")
						{
							$old_recurring_type = "Weekly";
						}
						if($new_recurring_type === "O")
						{
							$nrt = "One-time Event";
						}
						else if($new_recurring_type === "A")
						{
							$nrt = "Annually";
						}
						else if($new_recurring_type === "M")
						{
							$nrt = "Monthly";
						}
						else if($new_recurring_type === "W")
						{
							$nrt = "Weekly";
						}
						$body .= "	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-\"Recurring Type\" has been changed from \"{$ort}\" to: <b>\"{$nrt}\"</b>\n.<br>";
					}
					$signature = "<br>\nNote:  To disable these email notifications, simply adjust your Settings within the Grouple application.\n<br>As always, thanks for using Grouple!<br><br>\n\n-Grouple Support<br><br>\n\n{$signatureRandomizer}";
					$body .="{$signature}";
					
					//send the email (port, username, and password are stored in external includes)
					$smtp = @Mail::factory('smtp',  
					array ('host' => $host,  
					'port' => $port,  
					'auth' => true,   
					'username' => $username,  
					'password' => $password));
					$mail = @$smtp->send($to, $headers, $body);
					if (@PEAR::isError($mail)) 
					{   
						echo "Unable to process email.\n";		
					} 
					else 
					{ 
						echo "Successfully sent a event request email notification\n";	
					}
				}
			}
			echo "Done emailing all users of the event... will exit now!\n";
		}
		else
		{
			echo "No participants found for that eid... no emails will be sent.";
			exit();
		}
	}
	else
	{
		echo "Usage:\nphp mail.php F sender receiver\nOR\nphp mail.php G sender receiver gid\nOR\nphp mail.php E sender receiver eid\nOR\nphp mail.php EU sender eid old_ename old_startdate old_enddate old_category old_about old_location old_recurringtype";
		exit();
	}
	$mysqli->close();
	
?>
