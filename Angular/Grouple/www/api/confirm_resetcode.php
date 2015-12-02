<?php
	//This php will attempt to generate a reset code for the specified user, and will send an email to them containing the code.

	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(isset($_POST['email']) && isset($_POST['code']))
	{
		$email = $_POST['email'];
		$code = $_POST['code'];
		$stmt = $mysqli->prepare("SELECT reset_code FROM `users` WHERE email = ?");
		$stmt->bind_param('s', $email);
	}
	else
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, code)";
		echo json_encode ( $result );
		exit();	
	}

	$stmt->execute();
	$stmt->store_result();
	$stmt->bind_result($reset_code);
	$row_cnt = $stmt->num_rows;
	
	if($row_cnt > 0)
	{	
		$stmt->fetch();
		
		//check that user supplied reset code matches reset code from database
		if(strcmp($code, $reset_code) == 0)
		{		
			$result["success"] = 1;
			$result["message"] = "Code has been verified!";
		}
		else
		{
			$result["success"] = 0;
			$result["message"] = "Incorrect Reset Code.  Please try again or request a new code!";	
		}
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Please verify your email address is correct!";		
	}	
	
	$stmt->close();
	echo(json_encode($result));
	$mysqli->close();
?>
