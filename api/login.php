<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	#TODO gotta get the stay_logged value
	$_POST = json_decode(file_get_contents("php://input"), true);

	//attempt to login using email/session token
	if(isset($_POST['token']))
	{
		$stmt = $mysqli->prepare("SELECT token FROM users WHERE email = ?");
		$checkAgainst = $_POST['token'];
	}
	else if(!isset($_POST['email']) || !isset($_POST['password']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (email or password)";
		echo json_encode ($response);
		exit();
	}
	//atempt to login using email/password
	else
	{
		$stmt = $mysqli->prepare("SELECT password FROM users WHERE email = ?");
		$checkAgainst = $_POST['password'];
	}

	$stmt->bind_param('s', $_POST['email']);
	$stmt->execute();
	$result = $stmt->get_result();
	$row = $result->fetch_row();
	
	if(mysqli_num_rows($result) < 1)
	{
		$response["success"] = 0;
		$response["message"] = "That email address is not registered!";
		echo json_encode ($response);	
	}
	else
	{
		if(password_verify($checkAgainst, $row[0]))
		{
			//generate user a session token, store a hashed version in user row, and return it with response
			session_start();
			session_regenerate_id();
			$id = session_id();
			$_SESSION = array();
			session_destroy();
			$token_hash = password_hash($id, PASSWORD_DEFAULT);
			if($token_hash == false)
			{
				$response["success"] = 0;
				$response["message"] = "Internal server problem!  Please inform admin.";
				echo json_encode ($response);
				exit();
			}
			$stmt = $mysqli->prepare("UPDATE users SET token = ? WHERE email = ?");
			if($stmt === false)
			{
				$response["success"] = 0;
				$response["message"] = "MySQL error.";
				echo json_encode ($response);
				exit();
			}
			$stmt->bind_param('ss', $token_hash, $_POST['email']);
			if($stmt->execute())
			{
				$response["success"] = 1;
				$response["message"] = "Success! Logging in...";
				$response["token"] = $id;
				echo json_encode ($response);
			}
			else
			{
				$response["success"] = 0;
				$response["message"] = "Error querying server!";
				echo json_encode ($response);
			}		
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "Incorrect username or password!";
			echo json_encode ($response);
		}		
	}	
	$mysqli->close();
?>