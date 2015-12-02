<?php
	include_once('db_connect.inc.php');
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_GETT['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required GET parameters (id)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT id, name, email FROM items_tobring where e_id = ?");
	$stmt->bind_param('s', $_GET['id']);
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
		$response["success"] = 0;
		$response["message"] = "No items to bring."; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
