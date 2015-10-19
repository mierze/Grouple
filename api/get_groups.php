<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	//may wanna union to grab name and this here
	if(!isset($_POST['id']) || !isset($_POST['user']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (id or user)";
		echo json_encode($result);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT g.g_name, g.g_id FROM groups g INNER JOIN g_members gm ON gm.g_id = g.g_id where gm.rec_date is not null and gm.email = ? order by g.g_name");	
	$stmt->bind_param('s', $_POST['id']);
	$stmt->execute();
	$stmt->bind_result($name, $id);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;
	$response = array();
	if($row_cnt > 0)
	{
		$result = array();
		$response["success"] = 1;
		$response["items"] = array();
		while($stmt->fetch())
		{
			$result["name"]= $name;
			$result["id"]= $id;
			array_push($response["items"], $result);
		}
	}
	else
	{
		$response["success"] = 2;
		$response["message"] = "You are not in any groups!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>