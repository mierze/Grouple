<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	if(!isset($_POST['id']) || !isset($_POST['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id or user)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT u.email, u.first, u.last FROM users u INNER JOIN friends f ON f.sender=u.email WHERE f.receiver= ? AND rec_date IS NOT NULL
	UNION SELECT u.email, u.first, u.last FROM users u INNER JOIN friends f ON f.receiver=u.email WHERE f.sender= ? AND rec_date IS NOT NULL ORDER BY last");
	$stmt->bind_param('ss', $_POST['id'], $_POST['id']);
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
		$response["message"] = "You don't have any friends yet!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
