<?php

	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['e_id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (e_id)";
		echo json_encode ( $result );
		exit();
	} 

	$stmt = $mysqli->prepare("SELECT id, name, email FROM items_tobring where e_id = ?");
	$stmt->bind_param('s', $_POST['e_id']);
	$stmt->execute();
	$stmt->bind_result($id, $name, $email);
	$stmt->store_result();	
	$row_cnt = $stmt->num_rows;

	$response = array();
	if($row_cnt > 0)
	{
		$response["success"] = 1;
		$response["itemsToBring"] = array();
		while($stmt->fetch())
		{
			$result = array();
                        $result["id"] = $id;
			$result["name"] = $name;
			$result["email"] = $email;
			array_push($response["itemsToBring"], $result);
		}
		 
	}
	else
	{
		$response["success"] = 2;
		$response["message"] = "No items to bring."; 
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
	
	

?>
