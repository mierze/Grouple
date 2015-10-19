<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (email)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT name, b_level, rec_date from badges where email = ?");
	$stmt->bind_param('s', $_POST['id']);
	$stmt->execute();
	$stmt->bind_result($name, $level, $date);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{
		$response["success"] = 1;
		$response["items"] = array();
		while($stmt->fetch())
		{
			$result = array();
			$result["name"] = $name;
			$result["level"] = $level;
			$result["date"] = $date;
			array_push($response["items"], $result);
		} 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "You have no badges to display!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
