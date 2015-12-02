<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['user']) || !isset($_POST['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (user or id)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("UPDATE g_members SET rec_date = CURRENT_TIMESTAMP where email = ? AND g_id = ?");
	if($stmt === false)
	{
		$response["success"] = -1;
		$response["message"] = "Invalid SQL statement.";
		echo json_encode($response);
		exit();
	}
	$stmt->bind_param('ss', $_POST['user'], $_POST['id']);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$response["success"] = 1;
			$response["message"] = "Group invite accepted!";
			echo json_encode($response);
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "Group invite was not found!";
			echo json_encode($response);
		}
	}
	else
	{
		$response["success"] = -2;
		$response["message"] = "SQL execution fail.";
		echo json_encode($response);
	}		
	$mysqli->close();
?>