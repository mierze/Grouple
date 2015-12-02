<?php
	//This php will return the complete profile of a given user in the users table

	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(isset($_POST['email']))
	{
		$id = $_POST['email'];
		$stmt = $mysqli->prepare("SELECT image FROM users WHERE email = ?");
		$stmt->bind_param('s', $id);
	}
	else if(isset($_POST['gid']))
	{
		$id = $_POST['gid'];
		$stmt = $mysqli->prepare("SELECT image FROM groups WHERE g_id = ?");
		$stmt->bind_param('i', $id);
	}
	else if(isset($_POST['eid']))
	{
		$id = $_POST['eid'];
		$stmt = $mysqli->prepare("SELECT image FROM events WHERE e_id = ?");
		$stmt->bind_param('i', $id);
	}
	else
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters ((e,g)id)";
		echo json_encode ( $result );
		exit();	
	}


	$stmt->execute();
	$stmt->store_result();
	$stmt->bind_result($image);
	$row_cnt = $stmt->num_rows;
	
	if($row_cnt > 0)
	{	
		$stmt->fetch();
		$response["success"] = 1;
		$imgenc = base64_encode($image);
		$response["image"] = $imgenc;	
	}
	else
	{
		if(isset($_POST['email']))
		{
			$response["success"] = 2;
			$response["message"] = "No user image found for that email!";
		}
		else if(isset($_POST['gid']))
		{
			$response["success"] = 2;
			$response["message"] = "No group image found for that gid!";
		}
		else if(isset($_POST['eid']))
		{
			$response["success"] = 2;
			$response["message"] = "No event image found for that eid!";
		}		
	}	
	
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>