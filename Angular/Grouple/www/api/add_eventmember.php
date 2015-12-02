<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['sender']) || !isset($_POST['e_id']) || !isset($_POST['g_id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, sender, e_id, g_id)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email= $_POST['email'];
	$sender = $_POST['sender'];
	$e_id = $_POST['e_id'];
	$g_id = $_POST['g_id'];
	
	//check to see if event is currently joinable before adding member
	$stmt = $mysqli->prepare("SELECT joinable FROM events WHERE e_id = ?");
	if($stmt === false)
	{
		$result["success"] = 0;
		$result["message"] = "Invalid SQL statement.(#1)";
		echo json_encode ( $result );
		exit();
	}

	$stmt->bind_param('s', $e_id);
	
	if($stmt->execute())
	{
	    $stmt->bind_result($joinable);
		$stmt->store_result();
		$stmt->fetch();
	}
	else
	{
	    $result["success"] = 0;
	    $result["message"] = "Failed to add member.";
	    echo json_encode ( $result );
		exit();
	}		
			
	//check value of joinable. must be "Yes" to add new members
	if($joinable === 'Yes')
	{		

		//put user into e_groups table.  if group has already been credited for that event by that user, it will fail to add because of duplicate.
		$stmt = $mysqli->prepare("INSERT INTO e_groups (email, e_id, g_id) VALUES (?,?,?)");
		if($stmt === false)
		{
			$result["success"] = 0;
			$result["message"] = "Invalid SQL statement.(#2)";
			echo json_encode ( $result );
			exit();
		}

		$stmt->bind_param('sss', $email, $e_id, $g_id);
		if($stmt->execute())
		{
			//adding a non-creator to e_members table		
			$stmt = $mysqli->prepare("INSERT INTO e_members (email, sender, e_id) VALUES (?,?,?)");
			if($stmt === false)
			{
				$result["success"] = 0;
				$result["message"] = "Invalid SQL statement.(#3)";
				echo json_encode ( $result );
				exit();
			}

			$stmt->bind_param('sss', $email, $sender, $e_id);
		
			if($stmt->execute())
			{
				$result["success"] = 1;
				$result["message"] = "member has been added to the event.";
				echo json_encode ( $result );
							
				//Now that finished adding eventmate, initiate php (IN BACKGROUND) to handle email notification of this invite.  Because it runs in background, process will have minimal effects on JSON return response.
				// Note: potential improvement if used pclose(popen(command, "r")) instead.
				$WshShell = new COM("WScript.Shell");
				$oExec = $WshShell->Run("C:/wamp/bin/php/php5.5.12/php.exe -f C:/wamp/www/android_connect/mail.php E ".$sender.' '.$email.' '.$e_id, 0, false);
			}
			else
			{
				$result["success"] = 0;
				$result["message"] = "Failed to add member. (already member of e_member)";
				echo json_encode ( $result );
			}		
		}
		else
		{
			$result["success"] = 0;
			$result["message"] = "Failed to add member (e_groups) will only happen if a group invites the same member to the same event twice. blocks so no double credit.";
			echo json_encode ( $result );
		}	
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Event is no longer in a joinable state.";
		echo json_encode ( $result );
	}
			
	
	$mysqli->close();
?>