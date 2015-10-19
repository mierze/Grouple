<?php
	//This php removes all entries of a single group from the groupsnewtable

	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['e_id']) || !isset($_POST['type']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (e_id, type)";
		echo json_encode ( $result );
		exit();
	} 

	if ($_POST['type'] == "delete")
	{
		$stmt = $mysqli->prepare("DELETE from events WHERE e_id = ?");
		$stmt->bind_param('s', $_POST['e_id']);
	}
	else
	{
		$stmt = $mysqli->prepare("UPDATE events set hidden = 1 WHERE e_id = ?");
		$stmt->bind_param('s', $_POST['e_id']);
	}
	
	if($stmt === false)
	{
		echo "error in sql";
	}
	
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$result["success"] = 1;
			$result["message"] = "Event has been sucessfully deleted!";
			echo json_encode ( $result );
		}
		else
		{
			$result["success"] = 2;
			$result["message"] = "Event was not found!";
			echo json_encode ( $result );
		}
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "SQL failed to execute.";
		echo json_encode ( $result );
	}		
	
	$mysqli->close();
	
?>