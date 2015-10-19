<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['password']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, password)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email= $_POST['email'];
	$password= $_POST['password'];

	//first check if specified password is correct for specified user
	$stmt = $mysqli->prepare("SELECT password FROM users WHERE email = ?");
	$stmt->bind_param('s', $email);
	if($stmt->execute())
	{
		$mysqlresult = $stmt->get_result();
		$row = $mysqlresult->fetch_row();
		if(mysqli_num_rows($mysqlresult) < 1)
		{
			$result["success"] = 0;
			$result["message"] = "That email address is not registered!";
			echo json_encode ( $result );	
			exit();
		}
		else
		{
			//make sure hash password matches database hash
			if(password_verify($password, $row[0]))
			{
				//null token, and update setForDeletion tag to current timestamp.  mysqleventscheduler will clear row 30 days after.
				$stmt = $mysqli->prepare("UPDATE users SET setForDeletion=CURRENT_TIMESTAMP, token= NULL WHERE email = ?");
				$stmt->bind_param('s', $email);
				if($stmt->execute())
				{
					$result["success"] = 1;
					$result["message"] = "Account successfully flagged for deletion";
					echo json_encode ( $result );
				}
				else
				{
					$result["success"] = 0;
					$result["message"] = "Internal server problem! Please try again later.";
					echo json_encode ( $result );
				}
			}
			else
			{
				$result["success"] = 0;
				$result["message"] = "Incorrect password specified! Please try again.";
				echo json_encode ( $result );
			}
		}
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Internal server problem! Please try again later.";
		echo json_encode ( $result );
	}
	$mysqli->close();
?>