<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	$_POST = json_decode(file_get_contents("php://input"), true);
	$email = $_POST['email'];
	$first = $_POST['first'];
	$last = $_POST['last'];
	$password = $_POST['password'];
	if(!isset($email))
	{
		$result["success"] = -1;
		$result["message"] = "Missing required POST parameters (email)";
		echo json_encode ( $result );
		exit();
	} 

	#Validate E-mail
	#Source: http://www.w3schools.com/php/php_form_url_email.asp
	if(!filter_var($email, FILTER_VALIDATE_EMAIL))
	{
		$result["success"] = 3;
		$result["message"] = "Invalid email format";
		echo json_encode ( $result );
		$mysqli->close();
		exit();
	}
	
	#Validate password min/max length 
	if(strlen($password) < 8 || strlen($password) > 24)
	{
		$result["success"] = 4;
		$result["message"] = "Password must be between 8 and 24 characters.";
		echo json_encode ( $result );
		$mysqli->close();
		exit();
	}

	if(empty($first) || empty($last))
	{
		$result["success"] = 5;
		$result["message"] = "Please enter a first and last name.";
		echo json_encode ( $result );
		$mysqli->close();
		exit();
	}

	$stmt = $mysqli->prepare("SELECT COUNT(*) FROM users WHERE email = ?");
	$stmt->bind_param('s', $_POST['email']);
	$stmt->execute();
	$response = $stmt->get_result();
	$row = $response->fetch_row();

	if($row[0] == 0)
	{

		$password_hash = password_hash($_POST['password'], PASSWORD_DEFAULT);
		if($password_hash == false)
		{
			$result["success"] = 0;
			$result["message"] = "Internal server problem!  Please inform admin.";
			echo json_encode ( $result );
			exit();
		}
		$stmt = $mysqli->prepare("INSERT INTO users(email, password, first, last) VALUES (?, ?, ?, ?)");
		if($stmt === false)
		{
			$result["success"] = 0;
			$result["message"] = "MySQL error.";
			echo json_encode ( $result );
			exit();
		}

		$stmt->bind_param('ssss', $_POST['email'], $password_hash, $_POST['first'], $_POST['last']);
		if($stmt->execute())
		{
			$result["success"] = 1;
			$result["message"] = "Account registered successfully!";
			echo json_encode ( $result );
		}
		else
		{
			$result["success"] = 0;
			$result["message"] = "Unable to register account.";
		echo json_encode ( $result );
		}		
	}
	else
	{
		$result["success"] = 2;
		$result["message"] = "That email address already registered!";
		echo json_encode ( $result );
	}	
	$mysqli->close();
?>