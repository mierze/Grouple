<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	if(!isset($_GET['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required GET parameters (id)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT u.email, u.first, u.last FROM users u INNER JOIN g_members gm ON gm.email = u.email WHERE gm.g_id = ? AND gm.rec_date IS NOT NULL ORDER BY u.last");
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
		$response["message"] = "There are no members in this group!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
