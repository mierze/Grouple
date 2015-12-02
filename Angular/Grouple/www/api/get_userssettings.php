<?php

	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']))
	{
		$result["success"] = -1;
		$result["message"] = "Missing required POST parameters (email)";
		echo json_encode ( $result );
		exit();
	} 

	$stmt = $mysqli->prepare("SELECT emailFriendReq, emailGroupReq, emailEventReq, emailFriendMessage, emailGroupMessage, emailEventMessage, emailEventUpcoming, androidFriendReq, androidGroupReq, androidEventReq, androidFriendMessage, androidGroupMessage, androidEventMessage, androidEventUpcoming, androidUmbrella, emailUmbrella FROM users_settings WHERE email = ?");
	$stmt->bind_param('s', $_POST['email']);
	$stmt->execute();
	$stmt->bind_result($emailFriendReq, $emailGroupReq, $emailEventReq, $emailFriendMessage, $emailGroupMessage, $emailEventMessage, $emailEventUpcoming, $androidFriendReq, $androidGroupReq, $androidEventReq, $androidFriendMessage, $androidGroupMessage, $androidEventMessage, $androidEventUpcoming, $androidUmbrella, $emailUmbrella);
	$stmt->store_result();

	$row_cnt = $stmt->num_rows;

	$response = array();

	//a user setting file was found
	if($row_cnt > 0)
	{
		$response["success"] = 1;
		$response["settings"] = array();
		$stmt->fetch();		
		
		$result = array();
		$result["emailFriendReq"] = $emailFriendReq;
		$result["emailGroupReq"] = $emailGroupReq;
		$result["emailEventReq"] = $emailEventReq;
		$result["emailFriendMessage"] = $emailFriendMessage;
		$result["emailGroupMessage"] = $emailGroupMessage;
		$result["emailEventMessage"] = $emailEventMessage;
		$result["emailEventUpcoming"] = $emailEventUpcoming;
		$result["emailUmbrella"] = $emailUmbrella;

		$result["androidFriendReq"] = $androidFriendReq;
		$result["androidGroupReq"] = $androidGroupReq;
		$result["androidEventReq"] = $androidEventReq;
		$result["androidFriendMessage"] = $androidFriendMessage;
		$result["androidGroupMessage"] = $androidGroupMessage;
		$result["androidEventMessage"] = $androidEventMessage;
		$result["androidEventUpcoming"] = $androidEventUpcoming;
		$result["androidUmbrella"] = $androidUmbrella;

		array_push($response["settings"], $result);	 
	}
	//user has no settings file. create default and then return 	it.
	else
	{
	
		$stmt = $mysqli->prepare("INSERT INTO users_settings (email) VALUES (?)");
		$stmt->bind_param('s', $_POST['email']);
		
		//user settings row was added
		if($stmt->execute())
		{
			//now get user setttings and return as expected.

			$stmt = $mysqli->prepare("SELECT emailFriendReq, emailGroupReq, emailEventReq, emailFriendMessage, emailGroupMessage, emailEventMessage, emailEventUpcoming, androidFriendReq, androidGroupReq, androidEventReq, androidFriendMessage, androidGroupMessage, androidEventMessage, androidEventUpcoming, androidUmbrella, emailUmbrella FROM users_settings WHERE email = ?");
			$stmt->bind_param('s', $_POST['email']);
			$stmt->execute();
			$stmt->bind_result($emailFriendReq, $emailGroupReq, $emailEventReq, $emailFriendMessage, $emailGroupMessage, $emailEventMessage, $emailEventUpcoming, $androidFriendReq, $androidGroupReq, $androidEventReq, $androidFriendMessage, $androidGroupMessage, $androidEventMessage, $androidEventUpcoming, $androidUmbrella, $emailUmbrella);
			$stmt->store_result();

			$row_cnt = $stmt->num_rows;

			$response = array();

			//a user setting file was found
			if($row_cnt > 0)
			{
				$response["success"] = 1;
				$response["settings"] = array();
				$stmt->fetch();		
		
				$result = array();
				$result["emailFriendReq"] = $emailFriendReq;
				$result["emailGroupReq"] = $emailGroupReq;
				$result["emailEventReq"] = $emailEventReq;
				$result["emailFriendMessage"] = $emailFriendMessage;
				$result["emailGroupMessage"] = $emailGroupMessage;
				$result["emailEventMessage"] = $emailEventMessage;
				$result["emailEventUpcoming"] = $emailEventUpcoming;
				$result["emailUmbrella"] = $emailUmbrella;

				$result["androidFriendReq"] = $androidFriendReq;
				$result["androidGroupReq"] = $androidGroupReq;
				$result["androidEventReq"] = $androidEventReq;
				$result["androidFriendMessage"] = $androidFriendMessage;
				$result["androidGroupMessage"] = $androidGroupMessage;
				$result["androidEventMessage"] = $androidEventMessage;
				$result["androidEventUpcoming"] = $androidEventUpcoming;
				$result["androidUmbrella"] = $androidUmbrella;

				array_push($response["settings"], $result);	 
			}
			//should never encounter
			else
			{
				$response["success"] = 0;
				$response["message"] = "Unable to load users_settings information!"; 
			}
		} 
		else
		{
			$response["success"] = 0;
			$response["message"] = "No user with that email found!";
		}
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();

?>
