<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	if (!isset($_POST['from']) || !isset($_POST['to']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (from or to)";
		echo json_encode($response);
		exit();
	}
	$to = $_POST['to'];
	$from = $_POST['from'];
	$stmt = $mysqli->prepare("INSERT INTO friends(sender, receiver) VALUES (?, ?)");
	$stmt->bind_param('ss', $from, $to);
	$checkPending = $mysqli->prepare("SELECT COUNT(*) FROM FRIENDS WHERE sender = ? and receiver = ? AND REC_DATE IS NULL");
	$checkPending->bind_param('ss', $from, $to);
	$checkFriends = $mysqli->prepare("SELECT sender FROM FRIENDS WHERE sender = ? and receiver = ? AND REC_DATE IS NOT NULL UNION SELECT sender FROM FRIENDS WHERE sender = ? and receiver = ? AND REC_DATE IS NOT NULL");
	$checkFriends->bind_param('ssss', $from, $to, $to, $from);
	//check if user is attempting to friend self
	if (strcmp($to, $from) == 0)
	{
    	$response["success"] = 4;
		$response["message"] = "You can't friend request yourself!";
		echo json_encode($response);
	}
	else
	{
		//check if there is already a pending friend request
		if($checkPending->execute())
		{
			$res = $checkPending->get_result();
			$row = $res->fetch_row();
			if($row[0] == 1)
			{
				$response["success"] = 3;
				$response["message"] = "A friend request to that user is already pending.";
				echo json_encode($response);
			}
			//check if they are already friends
			else 
			{
				if($checkFriends->execute())
				{
					$res = $checkFriends->get_result();
					$row_cnt = $res->num_rows;
					if($row_cnt == 1)
					{
						$response["success"] = 2;
						$response["message"] = "You are already friends with that user.";
						echo json_encode($response);
					}
					//proceed with friend request
					else
					{
						if($stmt->execute())
						{
							$response["success"] = 1;
							$response["message"] = "Friend invite sent!";
							echo json_encode($response);
							//Now that finished adding friend, initiate php (IN BACKGROUND) to handle email notification of this invite.  Because it runs in background, process will have minimal effects on JSON return response.
							// Note: potential improvement if used pclose(popen(command, "r")) instead.
							$WshShell = new COM("WScript.Shell");
							$oExec = $WshShell->Run("C:/wamp/bin/php/php5.5.12/php.exe -f C:/wamp/www/android_connect/mail.php F " . $from . ' ' . $to, 0, false);			
						}
						else
						{
							$response["success"] = 0;
							$response["message"] = "That user does not exist!";
							echo json_encode($response);
						}
					}
				}
				else
				{
					$response["success"] = -1;
					$response["message"] = "Unable to process request.";
					echo json_encode($response);
				}
			}
		}	
		else
		{
			$response["success"] = -2;
			$response["message"] = "Unable to process request.";
			echo json_encode($response);
		}		
	}
	$mysqli->close();
?>