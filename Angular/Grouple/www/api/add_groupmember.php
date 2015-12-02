<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['sender']) || !isset($_POST['role']) || !isset($_POST['g_id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, sender, role, g_id)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email= $_POST['email'];
	$sender = $_POST['sender'];
	$role = $_POST['role'];
	$g_id = $_POST['g_id'];

	//adding a creator (rec_date set automatically),
	//therefore, creator will automatically become a member of the group.
	if (strcmp($role, "C") == 0) 
	{
	    $role = "A";
		$stmt = $mysqli->prepare("INSERT INTO g_members (email, sender, g_id, role, rec_date) 
		VALUES (?,'',?,?, CURRENT_TIMESTAMP)");
		if($stmt === false)
		{
			$result["success"] = 0;
			$result["message"] = "Invalid SQL statement.(#1)";
			echo json_encode ( $result );
			exit();
		}
		$stmt->bind_param('sss', $email, $g_id, $role);
	}
	//adding a non-creator (admin or regular member)
	else
	{			
		$stmt = $mysqli->prepare("INSERT INTO g_members (email, g_id, sender, role) 
		VALUES (?,?,?,?)");
		if($stmt === false)
		{
			$result["success"] = 0;
			$result["message"] = "Invalid SQL statement.(#1)";
			echo json_encode ( $result );
			exit();
		}

		$stmt->bind_param('ssss', $email, $g_id, $sender, $role);
	}

	if($stmt->execute())
	{
	    $result["success"] = 1;
   	    $result["message"] = "member has been added to the group.";
   	    echo json_encode ( $result );
		
		if(strcmp($role, "C") != 0)
		{
			//Now that finished adding groupmate, initiate php (IN BACKGROUND) to handle email notification of this invite.  Because it runs in background, process will have minimal effects on JSON return response.
			// Note: potential improvement if used pclose(popen(command, "r")) instead.
			$WshShell = new COM("WScript.Shell");
			$oExec = $WshShell->Run("C:/wamp/bin/php/php5.5.12/php.exe -f C:/wamp/www/android_connect/mail.php G ".$sender.' '.$email.' '.$g_id, 0, false);
		}	
	}
	else
	{
	    $result["success"] = 0;
	    $result["message"] = "Failed to add member.";
	    echo json_encode ( $result );
	}		
	
	$mysqli->close();
?>