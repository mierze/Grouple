<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	if(!isset($_GET['id']) || !isset($_GET['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required GET parameters (id or user)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT u.email, u.first, u.last FROM users u INNER JOIN friends f ON f.sender=u.email WHERE f.receiver= ? AND rec_date IS NOT NULL
	UNION SELECT u.email, u.first, u.last FROM users u INNER JOIN friends f ON f.receiver=u.email WHERE f.sender= ? AND rec_date IS NOT NULL ORDER BY last");
	$stmt->bind_param('ss', $_GET['id'], $_GET['id']);
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
		//set a mod return
		strcmp($_GET['id'], $_GET['user']) == 0 ?
			$response['mod'] = 1 : $response['mod'] = 0;
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "You don't have any friends yet!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
