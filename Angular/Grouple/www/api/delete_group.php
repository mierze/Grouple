<?php
	//This php removes all entries of a single group from the groupsnewtable

	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (g_id)";
		echo json_encode ( $result );
		exit();
	} 

	$stmt = $mysqli->prepare("DELETE from groups WHERE g_id = ?");
	if($stmt === false)
	{
		echo "error in sql";
	}
	$stmt->bind_param('s', $_POST['g_id']);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$result["success"] = 1;
			$result["message"] = "Group has been sucessfully deleted!";
			echo json_encode ( $result );
		}
		else
		{
			$result["success"] = 2;
			$result["message"] = "Group was not found!";
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