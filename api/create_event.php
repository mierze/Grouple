<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['name']) || !isset($_POST['startDate']) || !isset($_POST['endDate'])
		|| !isset($_POST['category']) || !isset($_POST['about']) || !isset($_POST['location']) || !isset($_POST['minPart'])
	|| !isset($_POST['maxPart']) || !isset($_POST['creator']) || !isset($_POST['recurring']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (name, startDate, endDate, category, about, location, minPart, maxPart, creator, recurring)";
		echo json_encode($response);
		exit();
	} 
	$name = $_POST['name'];
	$startDate = $_POST['startDate'];
	$endDate = $_POST['endDate'];
	$category = $_POST['category'];
	$about = $_POST['about'];
	$location = $_POST['location'];
	$minPart = $_POST['minPart'];
	$maxPart = $_POST['maxPart'];
	$creator = $_POST['creator'];
	$recurring = $_POST['recurring'];
	//PANDA check if this is reproposed feature here.
	//PANDA clean up this file
	if(isset($_POST['recType']))
	{
		if($minPart === "1")
		{
			$stmt = $mysqli->prepare("INSERT INTO events (e_name, eventstate, reproposed, start_date, end_date, category, about, location, min_part, max_part, creator, recurring_type) 
		VALUES (?,'Confirmed',1,?,?,?,?,?,?,?,?,?)");
		}
		else
		{
			$stmt = $mysqli->prepare("INSERT INTO events (e_name, reproposed, start_date, end_date, category, about, location, min_part, max_part, creator, recurring_type) VALUES (?,1,?,?,?,?,?,?,?,?,?)");
		}
		if($stmt === false)
		{
			$response["success"] = 0;
			$response["message"] = "Invalid SQL statement.(#3)";
			echo json_encode($response);
			exit();
		}
		$stmt->bind_param('ssssssssss', $name, $startDate, $endDate, $category, $about, $location, $minPart, $maxPart, $creator, $recurring);
	}
	//normal event creation without pic
	else
	{
		if($minPart === "1")
		{
			$stmt = $mysqli->prepare("INSERT INTO events (e_name, eventstate, start_date, end_date, category, about, location, min_part, max_part, creator, recurring_type) 
		VALUES (?,'Confirmed',?,?,?,?,?,?,?,?,?)");
		}
		else
		{
			$stmt = $mysqli->prepare("INSERT INTO events (e_name, start_date, end_date, category, about, location, min_part, max_part, creator, recurring_type) 
		VALUES (?,?,?,?,?,?,?,?,?,?)");
		}
		if($stmt === false)
		{
			$response["success"] = 0;
			$response["message"] = "Invalid SQL statement.(#4)";
			echo json_encode($response);
			exit();
		}
		$stmt->bind_param('ssssssssss', $name, $startDate, $endDate, $category, $about, $location, $minPart, $maxPart, $creator, $recurring);
	}
	if($stmt->execute())
	{
	    //Get the e_id of newly created event so it can be used for adding members
	    $idStmt = $mysqli->prepare("SELECT e_id FROM events WHERE e_name=? AND about=? AND start_date = ?");
		if($idStmt === false)
		{
			$response["success"] = 0;
			$response["message"] = "Invalid SQL statement.(#5)";
			echo json_encode($response);
			exit();
		}
		$idStmt->bind_param('sss', $name, $about, $startDate);
		$idStmt->execute();
		$idStmt->bind_result($id);
		$idStmt->store_result();
		$row_cnt = $idStmt->num_rows;
		if($row_cnt > 0)
		{
			$response["success"] = 1;
			$response["message"] = "Event has been added";
			$idStmt->fetch();
			$response["id"] = $id;
		}
		//Now that event is created. we'll go ahead and add the creator too before returning.
		//(rec_date set automatically),
		//therefore, creator will automatically become a member of the event.
		$role = "A";
		$stmt = $mysqli->prepare("INSERT INTO e_members (email, e_id, role, rec_date) 
		VALUES (?,?,?, CURRENT_TIMESTAMP)");
		if($stmt === false)
		{
			$response["success"] = 0;
			$response["message"] = "Invalid SQL statement.(#6)";
			echo json_encode($response);
			exit();
		}
		$stmt->bind_param('sss', $creator, $id, $role);
		if(!$stmt->execute())
		{			
			//Since event was created but creator was unable to be added, go ahead and clean up event so it can be remade by the user.
			$stmt = $mysqli->prepare("DELETE FROM events WHERE e_id=?");
			if($stmt === false)
			{
				$response["success"] = 0;
				$response["message"] = "Invalid SQL statement.(#7)";
				echo json_encode($response);
				exit();
			}
			$stmt->bind_param('s', $id);
			$stmt->execute();
			$response["success"] = 0;
			$response["message"] = "Failed to add creator to event.  Event has been deleted and must be recreated.";
			echo json_encode($response);
			exit();
		}
		//event created and creator has been added. next thing we'll do is attempt to insert to_bring list.
		else
		{
			if(isset($_POST['toBring']) )
			{
				$stmt = $mysqli->prepare("INSERT INTO items_tobring (e_id, name) VALUES (?,?)");
				if($stmt === false)
				{	
					$response["success"] = 0;
					$response["message"] = "Invalid SQL statement.(#8)";
					echo json_encode($response);
					exit();
				}
				foreach ($_POST['toBring'] as $toBring => $value)
				{
					$stmt->bind_param('ss', $id, $value);
					$stmt->execute();
				}
			}
		}
		//last thing we'll do before returning is process image if it was sent.
		//check to see if a file named pic has been sent by the client and matches supported file types
		if(!empty($_FILES["pic"]))
		{
			//get filetype of image
			if(!getimagesize($_FILES['pic']['tmp_name'])){
			die('Please ensure you are uploading an image.');
			}			
			$type = getimagesize($_FILES['pic']['tmp_name']);
			// Create image from file
			switch(strtolower($type['mime']))
			{
				case 'image/jpg':
					$image = imagecreatefromjpeg($_FILES['pic']['tmp_name']);
					break;
				case 'image/jpeg':
					$image = imagecreatefromjpeg($_FILES['pic']['tmp_name']);
					break;
				case 'image/png':
					$image = imagecreatefrompng($_FILES['pic']['tmp_name']);
					break;
				case 'image/gif':
					$image = imagecreatefromgif($_FILES['pic']['tmp_name']);
					break;
				default:
					exit('Unsupported type: '.$_FILES['pic']['type']);
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
			
			//prepare statement to update using $data as our resized image
			$stmt = $mysqli->prepare("UPDATE events SET image='$data_or',
			image_mdpi = '$data_m', image_hdpi = '$data_h', image_xhdpi = '$data_l', image_xxhdpi= '$data_xl' WHERE e_id = ?");
			$stmt->bind_param('s', $id);
			$stmt->execute();
		}
		
		//find all groups that the event creator is a member of
		//may wanna union to grab name and this here
		$stmt = $mysqli->prepare("SELECT groups.g_id FROM groups INNER JOIN g_members ON g_members.g_id = groups.g_id where g_members.rec_date is not null and g_members.email = ?");
		$stmt->bind_param('s', $_POST['creator']);
		$stmt->execute();
		$stmt->bind_result($gid);
		$stmt->store_result();
		$row_cnt = $stmt->num_rows;
		if($row_cnt > 0)
		{			
			while($stmt->fetch())
			{
				//add an e_groups row to credit that group.
				$stmt2 = $mysqli->prepare("INSERT INTO e_groups (email, e_id, g_id, rec_date) 
				VALUES (?,?,?, CURRENT_TIMESTAMP)");
				$stmt2->bind_param('sss', $_POST['creator'], $id, $gid);
				$stmt2->execute();
			}
		}
		else
		{
			$response["success"] = 2;
			$response["message"] = "No groups found."; 
		}
		echo json_encode ($result);
	}
	else
	{
	    $response["success"] = 0;
	    $response["message"] = "Failed to create event.\n" + "Name:" + $_POST['startDate'];// + $name+ "\nstartdate:"+ $startDate;//+ $endDate+ $category+ $about+ $location+ $minPart+ $maxPart+ $creator+ $recurring;
		echo json_encode($response);
		exit();
	}	
	$mysqli->close();
?>