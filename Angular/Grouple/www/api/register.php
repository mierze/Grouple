<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	if(!isset($_POST['email']))
	{
		$response["success"] = -1;
		$response["message"] = "Missing required POST parameters (email)";
	} 

	#Validate E-mail
	#Source: http://www.w3schools.com/php/php_form_url_email.asp
	else if(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL))
	{
		$response["success"] = 3;
		$response["message"] = "Invalid email format";
	}
	#Validate password min/max length 
	else if(strlen($_POST['password']) < 6 || strlen($_POST['password']) > 24)
	{
		$response["success"] = 4;
		$response["message"] = "Password must be between 6 and 24 characters.";
	}
	else if(empty($_POST['first']))
	{
		$response["success"] = 5;
		$response["message"] = "Please enter a first name.";
	}
	else
	{
		$stmt = $mysqli->prepare("SELECT COUNT(*) FROM users WHERE email = ?");
		$stmt->bind_param('s', $_POST['email']);
		$stmt->execute();
		$result = $stmt->get_result();
		$row = $result->fetch_row();
		if($row[0] == 0)
		{
			$password_hash = password_hash($_POST['password'], PASSWORD_DEFAULT);
			if($password_hash == false)
			{
				$response["success"] = -2;
				$response["message"] = "Internal server problem!  Please inform admin.";
			}
			$stmt = $mysqli->prepare("INSERT INTO users(email, password, first, last) VALUES (?, ?, ?, ?)");
			if($stmt === false)
			{
				$response["success"] = 0;
				$response["message"] = "MySQL error.";
			}
			$stmt->bind_param('ssss', $_POST['email'], $password_hash, $_POST['first'], $_POST['last']);
			if($stmt->execute())
			{
				$response["success"] = 1;
				$response["message"] = "Account registered successfully!";
			}
			else
			{
				$response["success"] = -1;
				$response["message"] = "Unable to register account.";
			}		
		}
		else
		{
			$response["success"] = 2;
			$response["message"] = "That email address already registered!";
		}
	}
	echo json_encode($response);
	$mysqli->close();
	exit();
?>