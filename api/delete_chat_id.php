<?php

	include_once('../db_connect.inc.php');

	$stmt = $mysqli->prepare("DELETE from chat_ids WHERE email = ?");
	if($stmt === false)
	{
		echo "error in sql";
	}
	$stmt->bind_param('s', $_POST['email']);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$result["success"] = 1;
			$result["message"] = "RegID has been removed.";
			echo json_encode ( $result );
		}
		else
		{
			$result["success"] = 2;
			$result["message"] = "RegID was not found!";
			echo json_encode ( $result );
		}
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Failed to write to database";
		echo json_encode ( $result );
	}		
	
	$mysqli->close();
	
?>