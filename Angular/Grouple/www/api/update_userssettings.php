<?php

	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['emailFriendReq']) || !isset($_POST['emailGroupReq']) || !isset($_POST['emailEventReq']) 
		|| !isset($_POST['emailFriendMessage']) || !isset($_POST['emailGroupMessage']) || !isset($_POST['emailEventMessage']) 
		|| !isset($_POST['emailEventUpcoming']) || !isset($_POST['emailFriendReq']) || !isset($_POST['emailGroupReq']) 
		|| !isset($_POST['emailEventReq']) || !isset($_POST['emailFriendMessage']) || !isset($_POST['emailGroupMessage']) 
		|| !isset($_POST['emailEventMessage']) || !isset($_POST['emailEventUpcoming']) || !isset($_POST['androidFriendReq']) 
		|| !isset($_POST['androidGroupReq']) || !isset($_POST['androidEventReq']) || !isset($_POST['androidFriendMessage']) 
		|| !isset($_POST['androidGroupMessage']) || !isset($_POST['androidEventMessage']) || !isset($_POST['androidEventUpcoming']) 
		|| !isset($_POST['androidUmbrella']) || !isset($_POST['emailUmbrella']))
	{
		$result["success"] = -1;
		$result["message"] = "Missing required POST parameters (email, emailFriendReq, emailGroupReq, emailEventReq, emailFriendMessage, emailGroupMessage, emailEventMessage, emailEventUpcoming, androidFriendReq, androidGroupReq, androidEventReq, androidFriendMessage, androidGroupMessage, androidEventMessage, androidEventUpcoming, androidUmbrella, emailUmbrella)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email = $_POST['email'];
	$emailFriendReq = $_POST['emailFriendReq'];
	$emailGroupReq = $_POST['emailGroupReq'];
	$emailEventReq = $_POST['emailEventReq'];
	$emailFriendMessage = $_POST['emailFriendMessage'];
	$emailGroupMessage = $_POST['emailGroupMessage'];
	$emailEventMessage = $_POST['emailEventMessage'];
	$emailEventUpcoming = $_POST['emailEventUpcoming'];
	$androidFriendReq = $_POST['androidFriendReq'];
	$androidGroupReq = $_POST['androidGroupReq'];
	$androidEventReq = $_POST['androidEventReq'];
	$androidFriendMessage = $_POST['androidFriendMessage'];
	$androidGroupMessage = $_POST['androidGroupMessage'];
	$androidEventMessage = $_POST['androidEventMessage'];
	$androidEventUpcoming = $_POST['androidEventUpcoming'];
	$androidUmbrella = $_POST['androidUmbrella'];
	$emailUmbrella = $_POST['emailUmbrella'];

	$stmt = $mysqli->prepare("UPDATE users_settings SET emailFriendReq=?, emailGroupReq=?, emailEventReq=?, emailFriendMessage=?, emailGroupMessage=?, emailEventMessage=?, emailEventUpcoming=?, androidFriendReq=?, androidGroupReq=?, androidEventReq=?, androidFriendMessage=?, androidGroupMessage=?, androidEventMessage=?, androidEventUpcoming=?, androidUmbrella=?, emailUmbrella=? WHERE email = ?");
	$stmt->bind_param('sssssssssssssssss', $emailFriendReq, $emailGroupReq, $emailEventReq, $emailFriendMessage, $emailGroupMessage, $emailEventMessage, $emailEventUpcoming, $androidFriendReq, $androidGroupReq, $androidEventReq, $androidFriendMessage, $androidGroupMessage, $androidEventMessage, $androidEventUpcoming, $androidUmbrella, $emailUmbrella, $email);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$result["success"] = 1;
			$result["message"] = "Settings successfully updated!";
		}
		else
		{
			//prepares statement to check if email was invalid or if settings was already set to that data
			$stmt = $mysqli->prepare("SELECT email FROM users_settings WHERE email = ?");
			$stmt->bind_param('s', $email);
			if($stmt->execute())
			{
				$stmt->store_result();
				if($stmt->num_rows > 0)
				{
					$result["success"] = 2;
					$result["message"] = "No changes necessary.";
				}
				else
				{
					$result["success"] = 0;
					$result["message"] = "Email entered was not found!";
				}
			}
		}
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "SQL error: Failed to write to database";
	}	
	
	$stmt->close();
	echo(json_encode($result));
	$mysqli->close();
?>
