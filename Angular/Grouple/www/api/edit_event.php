<?php
	//PANDA: take in email of modifier and log that in DB plus check for admin rights
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	
	if(!isset($_POST['id']) || !isset($_POST['name']) || !isset($_POST['public']) || !isset($_POST['about'])
	   || !isset($_POST['startDate']) || !isset($_POST['endDate'])
	   || !isset($_POST['category']) || !isset($_POST['minPart'])
	   || !isset($_POST['maxPart']) || !isset($_POST['recType'])
	   || !isset($_POST['recType']) || !isset($_POST['location']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id, name, public, about, startDate, endDate, category, minPart, maxPart, recType or location)";
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
		
		//prepare statement to update using $data as our resized image
		$stmt = $mysqli->prepare("UPDATE events SET e_name = ?, public = ?, about = ?, start_date = ?, end_date= ?, category = ?, min_part = ?, max_part = ?, location=?, recurring_type = ?, image='$data_or',
		image_mdpi = '$data_m', image_hdpi = '$data_h', image_xhdpi = '$data_l', image_xxhdpi= '$data_xl' WHERE e_id = ?");
		$stmt->bind_param('sssssssssss', $_POST['name'],$_POST['public'],$_POST['about'],$_POST['startDate'], $_POST['endDate'], $_POST['category'], $_POST['minPart'], $_POST['maxPart'], $_POST['location'], $_POST['recType'], $_POST['id']);
	}
	//update without image (has not been by user)
	else
	{
		$stmt = $mysqli->prepare("UPDATE events SET e_name = ?, public = ?, about = ?, start_date = ?, end_date = ?, category = ?, min_part = ?, max_part = ?, recurring_type = ?, location = ? WHERE e_id = ?");
		$stmt->bind_param('sssssssssss', $_POST['name'],$_POST['public'], $_POST['about'],$_POST['startDate'], $_POST['endDate'], $_POST['category'], $_POST['minPart'], $_POST['maxPart'], $_POST['recType'], $_POST['location'], $_POST['id']);
	}
	
	//store some old information about event of eid in order to generate a more useful email notification later
	$stmt2 = $mysqli->prepare("SELECT e_name, about, start_date, end_date, category, location, recurring_type FROM events WHERE e_id = ?");
	$stmt2->bind_param('s', $_POST['id']);
	if(!$stmt2->execute())
	{
		$response["success"] = 2;
		$response["message"] = "Error querying server!";
		echo json_encode($response);
		exit();
	}
	$stmt2->bind_result($old_e_name, $old_about, $old_start_date, $old_end_date, $old_category, $old_location, $old_recurring_type);
	$stmt2->store_result();
	$row_cnt = $stmt2->num_rows;
	if($row_cnt > 0)
	{
		$stmt2->fetch();
	}
	if($stmt->execute())
	{
		if($mysqli->affected_rows <= 0)
		{
			//prepares statement to check if eid was invalid or if profile was already set to that data
			$stmt = $mysqli->prepare("SELECT e_id FROM events WHERE e_id = ?");
			$stmt->bind_param('s', $_POST['id']);
			if($stmt->execute())
			{
				if($stmt->store_result())
				{
					$response["success"] = 2;
                    $response["message"] = "No changes necessary.";
					echo json_encode($response);
					exit();
				}
				else
				{
					$response["success"] = 0;
					$response["message"] = "ID supplied was not found!";
					echo json_encode($response);
					exit();
				}
			}
		}	
		//always delete old item list.  if new one has been specified, it will replace.  if new one has not been specified, that means list should be left empty.
        $itemDelStmt = $mysqli->prepare("DELETE FROM items_tobring WHERE e_id = ?");
        if($itemDelStmt === false)
        {	
			$response["success"] = 0;
            $response["message"] = "Invalid SQL statement.(DELETE ITEMS)";
            echo json_encode($response);
            exit();
        }            
		$itemDelStmt->bind_param('s', $_POST['id']);
        $itemDelStmt->execute();
		//check for items to add to list
        if(isset($_POST['toBring']))
        {
            $itemStmt = $mysqli->prepare("INSERT INTO items_tobring (e_id, name) VALUES (?,?)");
            if($itemStmt === false)
            {	
				$response["success"] = 0;
				$response["message"] = "Invalid SQL statement.(ADD ITEMS)";
                echo json_encode($response);
                exit();
            }

            foreach ($_POST['toBring'] as $toBring => $value)
            {
				$itemStmt->bind_param('ss', $_POST['id'], $value);
                $itemStmt->execute();
            }
			$response["success"] = 1;
			$response["message"] = "Event successfully updated!";
        }
        else
        {
            //no items to update, so we good
            $response["success"] = 1;
			$response["message"] = "Event successfully updated without toBring!";
        }
		function removeSpaces($str)
		{
			$new = str_replace(' ', '_', $str);
			return $new;
		}
		//$command = "C:\\wamp\\bin\\php\\php5.5.12\\php.exe -f C:\\wamp\\www\\android_connect\\mail.php EU ".$_POST['creator'].' '.$_POST['e_id'].' '.removeSpaces($old_e_name).' '.removeSpaces($old_start_date).' '.removeSpaces($old_end_date).' '.removeSpaces($old_category).' '.removeSpaces($old_about).' '.removeSpaces($old_location).' '.removeSpaces($old_recurring_type);
		//$response["activated"] = $command;
		echo json_encode($response);
		//Now that finished adding eventmate, initiate php (IN BACKGROUND) to handle email notification of this invite.  Because it runs in background, process will have minimal effects on JSON return response.
		// Note: potential improvement if used pclose(popen(command, "r")) instead.
		//$WshShell = new COM("WScript.Shell");
		//$oExec = $WshShell->Run($command, 0, false);
		//Usage: php mail.php EU sender eid old_ename old_startdate old_enddate old_category old_about old_location old_recurringtype"
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "Error querying server!";
		echo json_encode($response);
	}	
	$mysqli->close();   
?>