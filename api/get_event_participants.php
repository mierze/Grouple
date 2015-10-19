<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	if(!isset($_POST['id']) || !isset($_POST['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id or user)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT u.email, u.first, u.last FROM users u JOIN e_members em ON em.email = u.email WHERE em.e_id = ? AND em.rec_date IS NOT NULL ORDER BY u.last");
	$stmt->bind_param('s', $_POST['id']);
	$stmt->execute();
	$stmt->bind_result($email,$first,$last);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{	
		$result = array();
		$response["success"] = 1;
		$response["items"] = array();	
		while($stmt->fetch())
		{
			$result["first"] = $first;
			$result["last"] = $last;
			$result["email"] = $email;
			array_push($response["items"], $result);
		}
	}
	else
	{
		$response["success"] = 2;
		$response["message"] = "This event doesn't have any users attending!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
