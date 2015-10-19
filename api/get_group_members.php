<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	if(!isset($_POST['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT u.email, u.first, u.last FROM users u INNER JOIN g_members gm ON gm.email = u.email WHERE gm.g_id = ? AND gm.rec_date IS NOT NULL ORDER BY u.last");
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
		$response["message"] = "You don't have any friends yet!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
