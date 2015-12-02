<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	if (!isset($_POST['first']) || !isset($_POST['last']) || !isset($_POST['birthday'])
	   || !isset($_POST['about']) || !isset($_POST['location'])
	   || !isset($_POST['gender']) || !isset($_POST['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (first, last, birthday, about, location, gender or id)";
		echo json_encode($response);
		exit();
	}
	
	//check to see if a file named image has been sent by the client and matches supported file types
	if(!empty($_FILES["image"]))
	{
		//get filetype of image
		if(!getimagesize($_FILES['image']['tmp_name'])){
		die('Please ensure you are uploading an image.');
		}
		
		$type = getimagesize($_FILES['image']['tmp_name']);

		// Create image from file
		switch(strtolower($type['mime']))
		{
			case 'image/jpg':
				$image = imagecreatefromjpeg($_FILES['image']['tmp_name']);
				break;
			case 'image/jpeg':
				$image = imagecreatefromjpeg($_FILES['image']['tmp_name']);
				break;
			case 'image/png':
				$image = imagecreatefrompng($_FILES['image']['tmp_name']);
				break;
			case 'image/gif':
				$image = imagecreatefromgif($_FILES['image']['tmp_name']);
				break;
			default:
				exit('Unsupported type: '.$_FILES['image']['type']);
		}

		// Target dimensions	
		$max_width_or = 240;
		$max_height_or = 180;
		
		$max_width_m = 120;
		$max_height_m = 120;
		
		$max_width_h = 240;
		$max_height_h = 240;
		
		$max_width_l = 720;
		$max_height_l = 720;
		
		$max_width_xl = 1200;
		$max_height_xl = 1200;

		// Get current dimensions
		$old_width  = imagesx($image);
		$old_height = imagesy($image);

		// Calculate the 5 scaling sizes we need to do to fit the image inside our frame
		$scale_or = min($max_width_or/$old_width, $max_height_or/$old_height);
		$scale_m      = min($max_width_m/$old_width, $max_height_m/$old_height);
		$scale_h      = min($max_width_h/$old_width, $max_height_h/$old_height);
		$scale_l      = min($max_width_l/$old_width, $max_height_l/$old_height);
		$scale_xl      = min($max_width_xl/$old_width, $max_height_xl/$old_height);

		// Get the 5 new dimensions
		
		$new_width_or  = ceil($scale_or*$old_width);
		$new_height_or = ceil($scale_or*$old_height);
		
		$new_width_m  = ceil($scale_m*$old_width);
		$new_height_m = ceil($scale_m*$old_height);
		
		$new_width_h  = ceil($scale_h*$old_width);
		$new_height_h = ceil($scale_h*$old_height);
		
		$new_width_l  = ceil($scale_l*$old_width);
		$new_height_l = ceil($scale_l*$old_height);
		
		$new_width_xl  = ceil($scale_xl*$old_width);
		$new_height_xl = ceil($scale_xl*$old_height);

		// Create 5 new empty image
		
		$new_or = imagecreatetruecolor($new_width_or, $new_height_or);
		
		// Resize old image into 4 new
		imagecopyresampled($new_or, $image, 
		 0, 0, 0, 0, 
		 $new_width_or, $new_height_or, $old_width, $old_height);

		// Catch the imagedata
		ob_start();
		imagejpeg($new_or, NULL, 90);
		$data_or = ob_get_clean();
		$data_or = addslashes($data_or);
		
		$new_m = imagecreatetruecolor($new_width_m, $new_height_m);
		
		// Resize old image into 4 new
		imagecopyresampled($new_m, $image, 
		 0, 0, 0, 0, 
		 $new_width_m, $new_height_m, $old_width, $old_height);

		// Catch the imagedata
		ob_start();
		imagejpeg($new_m, NULL, 90);
		$data_m = ob_get_clean();
		$data_m = addslashes($data_m);
		
		$new_h = imagecreatetruecolor($new_width_h, $new_height_h);
		
		imagecopyresampled($new_h, $image, 
		 0, 0, 0, 0, 
		 $new_width_h, $new_height_h, $old_width, $old_height);

		// Catch the imagedata
		ob_start();
		imagejpeg($new_h, NULL, 90);
		$data_h = ob_get_clean();
		$data_h = addslashes($data_h);
		
		$new_l = imagecreatetruecolor($new_width_l, $new_height_l);
		
		imagecopyresampled($new_l, $image, 
		 0, 0, 0, 0, 
		 $new_width_l, $new_height_l, $old_width, $old_height);

		// Catch the imagedata
		ob_start();
		imagejpeg($new_l, NULL, 90);
		$data_l = ob_get_clean();
		$data_l = addslashes($data_l);
		
		$new_xl = imagecreatetruecolor($new_width_xl, $new_height_xl);
		
		imagecopyresampled($new_xl, $image, 
		 0, 0, 0, 0, 
		 $new_width_xl, $new_height_xl, $old_width, $old_height);

		// Catch the imagedata
		ob_start();
		imagejpeg($new_xl, NULL, 90);
		$data_xl = ob_get_clean();
		ob_end_clean();
		$data_xl = addslashes($data_xl);

		$stmt = $mysqli->prepare("UPDATE users SET first = ?, last = ?, birthday = ?, about = ?, location = ?, gender = ?, image='$data_or',
		image_mdpi = '$data_m', image_hdpi = '$data_h', image_xhdpi = '$data_l', image_xxhdpi= '$data_xl' WHERE email = ?");
		$stmt->bind_param('sssssss', $_POST['first'],$_POST['last'],$_POST['birthday'],$_POST['about'],$_POST['location'], $_POST['gender'], $_POST['id']);
	}
	//update without image (has not been set by user)
	else
	{
		$stmt = $mysqli->prepare("UPDATE users SET first = ?, last = ?, birthday = ?, about = ?, location = ?, gender = ? WHERE email = ?");
		$stmt->bind_param('sssssss', $_POST['first'],$_POST['last'],$_POST['birthday'],$_POST['about'],$_POST['location'], $_POST['gender'], $_POST['id']);
		$response["statement"] = $_POST["birthday"];
	}
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$response["success"] = 1;
			$response["message"] = "Profile successfully updated!";
			echo json_encode($response);
		}
		else
		{
			//prepares statement to check if email was invalid or if profile was already set to that data
			$stmt = $mysqli->prepare("SELECT email FROM users WHERE email = ?");
			$stmt->bind_param('s', $_POST['id']);
			if($stmt->execute())
			{
				if($stmt->store_result())
				{
					$response["success"] = 2;
					$response["message"] = "No changes necessary.";
					echo json_encode($response);
				}
				else
				{
					$response["success"] = 0;
					$response["message"] = "Email entered was not found!";
					echo json_encode($response);
				}
			}
		}
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "SQL error: Failed to write to database";
		echo json_encode($response);
	}	
	$mysqli->close();
?>