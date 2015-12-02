<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//attempt to login using email/session token
	if(isset($_POST['token']))
	{
		$stmt = $mysqli->prepare("SELECT token FROM users WHERE email = ?");
		$checkAgainst = $_POST['token'];
		//TODO finish logic
	}
	else if(!isset($_POST['email']) || !isset($_POST['password']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (email or password)";
		echo json_encode($response);
		exit();
	}
	//atempt to login using email/password
	else
	{
		$stmt = $mysqli->prepare("SELECT first, last, password FROM users WHERE email = ?");
		$checkAgainst = $_POST['password'];
		$stmt->bind_param('s', $_POST['email']);
		$stmt->execute();
		$stmt->bind_result($first, $last, $password);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;
		if($row_cnt > 0)
		{
			$result = array();
			$stmt->fetch();
			if(password_verify($checkAgainst, $password))
			{
				//generate user a session token, store a hashed version in user row, and return it with response
				session_start();
				session_regenerate_id();
				$id = session_id();
				$_SESSION = array();
				session_destroy();
				$token_hash = password_hash($id, PASSWORD_DEFAULT);
				if(!$token_hash == false)
				{
					$stmt = $mysqli->prepare("UPDATE users SET token = ? WHERE email = ?");
					if($stmt === false)
					{
						$response["success"] = -3;
						$response["message"] = "MySQL error.";
					}
					$stmt->bind_param('ss', $token_hash, $_POST['email']);
					if($stmt->execute())
					{
						$response["success"] = 1;
						$response["message"] = "Success! Logging in...";
						$response["token"] = $id;
						$response["email"] = $_POST['email'];
						$response["first"] = $first;
						$response["last"] = $last;
					}
					else
					{
						$response["success"] = -2;
						$response["message"] = "Error querying server!";
					}
				}
				else
				{
					$response["success"] = -4;
					$response["message"] = "Internal server problem! Please inform admin.";
				}
			}
			else
			{
				$response["success"] = -1;
				$response["message"] = "Incorrect username or password!";
			}		
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "That email address is not registered!";
		}
	}
	echo json_encode($response);	
	$mysqli->close();
	exit();
?>