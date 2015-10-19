<?php
	//This php will return the complete profile of a given user in the users table
	//PANDA: merge this with other get_profile_image files and take in a parameter size
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(isset($_POST['content']) && isset($_POST['id']))
	{
		if($_POST['content'] == 'user')
		{
			$stmt = $mysqli->prepare("SELECT image_hdpi FROM users WHERE email = ?");
		}
		else if ($_POST['content'] == 'group')
		{
			$stmt = $mysqli->prepare("SELECT image_hdpi FROM groups WHERE g_id = ?");
		}
		else if ($_POST['content'] == 'event')
		{
			$stmt = $mysqli->prepare("SELECT image_hdpi FROM events WHERE e_id = ?");
		}
		$id = $_POST['id'];
		$stmt->bind_param('s', $id);
	}
	else
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (content or id)";
		echo json_encode($response);
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
	{	//no image found
		$response["message"] = "No " . $_POST['content'] . " image found!";
		$response["success"] = 2;
	}	
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>