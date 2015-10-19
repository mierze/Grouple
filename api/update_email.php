<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['password']) || !isset($_POST['newEmail']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, password, newEmail)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email= $_POST['email'];
	$password= $_POST['password'];
	$newEmail= $_POST['newEmail'];
	
	#Validate E-mail
	#Source: http://www.w3schools.com/php/php_form_url_email.asp
	if(!filter_var($newEmail, FILTER_VALIDATE_EMAIL))
	{
		$result["success"] = 0;
		$result["message"] = "Invalid email format.";
		echo json_encode ( $result );
		$mysqli->close();
		exit();
	}

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
				//make sure newEmail is not already in use by another user.
				$stmt = $mysqli->prepare("SELECT email FROM users WHERE email = ?");
				$stmt->bind_param('s', $newEmail);
				if($stmt->execute())
				{
					$mysqlresult = $stmt->get_result();
					$row = $mysqlresult->fetch_row();
					if(mysqli_num_rows($mysqlresult) > 0)
					{
						$result["success"] = 0;
						$result["message"] = "That email address is already registered by another user!";
						echo json_encode ( $result );	
						exit();
					}
					else
					{
						//update email
						$stmt = $mysqli->prepare("UPDATE users SET email=? WHERE email = ?");
						$stmt->bind_param('ss', $newEmail, $email);
						if($stmt->execute())
						{
							$result["success"] = 1;
							$result["message"] = "Email updated successfully";
							echo json_encode ( $result );
						}
						else
						{
							$result["success"] = 0;
							$result["message"] = "Internal server problem! Please try again later.";
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