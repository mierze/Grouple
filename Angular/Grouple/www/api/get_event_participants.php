<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$response = array();
	if(!isset($_GET['id']) || !isset($_GET['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required GET parameters (id or user)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT u.email, u.first, u.last FROM users u JOIN e_members em ON em.email = u.email WHERE em.e_id = ? AND em.rec_date IS NOT NULL ORDER BY u.last");
	$stmt->bind_param('s', $_GET['id']);
	$stmt->execute();
	$stmt->bind_result($id,$first,$last);
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
			$result["id"] = $id;
			array_push($response["items"], $result);
		}
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "This event doesn't have any users attending!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
