<?php
	//This php removes a single user from a specified group

	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['gname']) || !isset($_POST['mem']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (gname, mem)";
		echo json_encode ( $result );
		exit();
	} 

	$stmt = $mysqli->prepare("DELETE from groupsnew WHERE g_name = ? AND member = ? AND rec_date IS NOT NULL");
	if($stmt === false)
	{
		echo "error in sql";
	}
	$stmt->bind_param('ss', $_POST['gname'], $_POST['mem']);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$result["success"] = 1;
			$result["message"] = "Group member has been removed.";
			echo json_encode ( $result );
		}
		else
		{
			$result["success"] = 2;
			$result["message"] = "Group member was not found!";
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