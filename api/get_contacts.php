<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	if (!isset($_POST['email']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (email)";
		echo json_encode($response);
		exit();
	}
	$stmt = $mysqli->prepare("SELECT id, message, read_date, sender, receiver, send_date, u.first, u.last, u.image_mdpi FROM messages, users u
							 WHERE u.email = receiver AND id in ( SELECT MAX(id) AS id FROM messages WHERE sender = ? GROUP BY receiver)
							 UNION SELECT id, message, read_date, sender, receiver, send_date, u.first, u.last, u.image_mdpi
							 FROM messages, users u WHERE u.email = sender AND id in ( SELECT MAX(id) AS id FROM messages
							 WHERE receiver = ? GROUP BY sender) order by id desc");
	$stmt->bind_param('ss', $_POST['email'], $_POST['email']);
	$stmt->execute();
	$stmt->store_result();
	$stmt->bind_result($id, $message, $readDate, $from, $to, $sendDate, $first, $last, $image);
	$row_cnt = $stmt->num_rows;	
	if($row_cnt > 0)
	{	
		$response["success"] = 1;
		$response["contacts"] = array();
		while($stmt->fetch())
		{
			$found = 0;
			strcmp($from, $_POST['email']) == 0 ? $contact = $to : $contact = $from;
			for ($i = 0; $i < count($response["contacts"]); $i++)
			{
				strcmp($response["contacts"][$i]["from"], $_POST['email']) == 0 ? $c = $response["contacts"][$i]["to"] : $c = $response["contacts"][$i]["from"];
				if (strcmp($c, $contact) == 0)
				{
					$found = 1;
				}
			}
			//check that newer message doesn't exist
			if ($found == 0)
			{
				$result = array();
				$result["id"] = $id;
				$result["from"] = $from;
				$result["to"] = $to;
				$result["contact"] = $contact;
				$result["message"] = $message;
				$result["sendDate"] = $sendDate;
				$result["readDate"] = $readDate;
				$result["first"] = $first;
				$result["last"] = $last;
				$imgenc = base64_encode($image);
				$result["image"] = $imgenc;	
				array_push($response["contacts"], $result);
			}
		}
	}
	else
	{
		$response["success"] = 2;
		$response["message"] = "You don't have any recent contacts!"; 
	}
	echo(json_encode($response));
?>