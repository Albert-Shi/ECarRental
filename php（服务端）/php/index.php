<?php
/**
 * Created by PhpStorm.
 * User: 史书恒
 * Date: 2016/9/13
 * Time: 18:26
 */
 
$servername = "localhost";
$username = "root";
$password = "19951230ssh";

$con = mysqli_connect($servername, $username, $password);

$update_allDayAddOne = 'update usersinfo set Day=Day+1';

function insertCarsInfo($connection, $number, $able, $style, $brand, $name, $people, $cargo, $price, $deposit, $distance, $note, $picture) {
    $insert_into_carsinfo = 'insert into carsinfo (Number, Able, Style, Brand, Name, People, Cargo, Price, Deposit, Distance, Picture, Note) VALUES ("'.$number.'","'.$able.'","'.$style.'","'.$brand.'","'.$name.'",'.$people.','.$cargo.','.$price.','.$deposit.', '.$distance.',"'.$picture.'","'.$note.'")';
//    echo "$insert_into_carsinfo<br>";
//    $a = $insert_into_carsinfo;
    if (mysqli_query($connection, $insert_into_carsinfo))
        return true;
    else
        return false;
}

function insertUsersInfo($connection, $account, $password, $admin, $name) {
    $insert_into_usersinfo = 'insert into usersinfo (Account, Password, Admin, Name, Cash) VALUES ("'.$account.'","'.$password.'",'.$admin.',"'.$name.'",0)';
//    $b = $insert_into_usersinfo;
    if (mysqli_query($connection, $insert_into_usersinfo))
        return true;
    else
        return false;
}

function updateInfo_common_single($connection, $table, $primarykey_field, $primarykey, $field, $newdata) {
    $update_query = 'update '.$table.' set '.$field.'='.$newdata.' where '.$primarykey_field.'='.$primarykey;
    if (mysqli_query($connection, $update_query))
        return true;
    else
        return false;
}

function updateUserInfo($connection, $account, $password, $admin, $name) {
    $update_usersinfo = 'update usersinfo set Password="'.$password.'", Admin='.$admin.', Name="'.$name.'" where Account="'.$account.'"';
    if (mysqli_query($connection, $update_usersinfo))
        return true;
    else
        return false;
}

function updateCarInfo($connection, $number, $able, $style, $brand, $name, $people, $cargo, $price, $deposit, $distance, $note, $picture) {
    $update_carsinfo = 'update carsinfo set Able='.$able.', Style="'.$style.'", Brand="'.$brand.'", Name="'.$name.'", People='.$people.', Cargo='.$cargo.', Price='.$price.', Deposit='.$deposit.', Distance='.$distance.', Picture="'.$picture.'", Note="'.$note.'" where Number="'.$number.'"';
    if (mysqli_query($connection, $update_carsinfo))
        return true;
    else
        return false;
}

function deleteInfo_common($connection, $table, $primarykey_field, $primarykey, $deleteall) {
    $delete_query = 'delete from '.$table.' where '.$primarykey_field.'='.$primarykey;
    $delete_query_all = 'delete from '.$table;
    if ($deleteall == true) {
        if (mysqli_query($connection, $delete_query_all))
            return true;
        else
            return false;
    } else if ($deleteall == false) {
        if (mysqli_query($connection, $delete_query))
            return true;
        else
            return false;
    }
}

function searchInfo($connection, $table, $field, $record, $symbol) {
    if ($symbol == 1) {
        $symbol = '=';
    }else if ($symbol == 2) {
        $symbol = '>';
    }else if ($symbol == 3) {
        $symbol = '<';
    }else if ($symbol == 4) {
        $symbol = '>=';
    }else if ($symbol == 5) {
        $symbol = '<=';
    }else if ($symbol == 6) {
        $symbol = '!=';
    }
    if ($table == 'carsinfo' || $table == 'CarsInfo') {
        $text = '{"info":"","data":{"userinfo":"","carinfo":[';
        if ($result_car = mysqli_query($connection, 'select * from carsinfo where '.$field.$symbol.$record)) {
            while ($row_car = $result_car->fetch_assoc()) {
                $text .= '{"number":"' . $row_car['Number'] . '","able":"' . $row_car['Able'] . '","style":"' . $row_car['Style'] . '","brand":"' . $row_car['Brand'] . '","name":"' . $row_car['Name'] . '","people":"' . $row_car['People'] . '","cargo":"' . $row_car['Cargo'] . '","price":"' . $row_car['Price'] . '","deposit":"'.$row_car['Deposit'].'","distance":"'.$row_car['Distance'].'","picture":"'.$row_car['Picture'].'","Note":"'.$row_car['note'].'"}';
            }
            $text = substr_replace($text, "", strlen($text)) . ']}}';
            echo $text;
        }
    } else if ($table == 'usersinfo' || $table == 'carsinfo') {
        $text = '{"info":"","data":{"userinfo":[';
        if ($result_user = mysqli_query($connection, 'select * from usersinfo where '.$field.$symbol.$record)) {
            while ($row_user = $result_user->fetch_assoc()) {
                $text .= '{"account":"'.$row_user['Account'].'","admin":"'.$row_user['Admin'].'","name":"'.$row_user['Name'].'","cash=":"' .$row_user['Cash']. '"},';
            }
            $text = substr_replace($text, "", strlen($text)) . '],"carinfo":""}}';
            echo $text;
        }
    }
}

function rentCar($connection, $account, $number, $startdate, $total) {
//    $q1 = 'update usersinfo set Number="'.$number.'", Startdate="'.$startdate.'", Day=1, Total='.$total.' where Account="'.$account.'"';
	$q1 = 'insert into rentinfo (Account, Number, Startdate, Day, Total) VALUES ("'.$account.'","'.$number.'","'.$startdate.'",1,'.$total.')';
    $q2 = 'update carsinfo set Able=0 where Number="'.$number.'"';
    if (mysqli_query($connection, $q1) && mysqli_query($connection, $q2))
		return true;
//        echo 'successful';
    else
		return false;
//        die(mysqli_error($connection));
//        echo 'failed';
}
function backCar($connection, $account, $number) {
//    $q1 = 'update usersinfo set Number="0", Startdate="0", Total=0 where Account="'.$account.'"';
    $q2 = 'update carsinfo set Able=1 where Number="'.$number.'"';
    if (mysqli_query($connection, $q2) && deleteInfo_common($connection, 'rentinfo', 'Number', '"'.$number.'"', false))
        return true;
    else
//		return false;
        die(mysqli_error($connection));
//        echo 'failed';
}

function getRentInfosFromUser($connection, $account) {	
	$q = 'select * from rentinfo where Account="'.$account.'"';
	if ($result_car = mysqli_query($connection, $q)) {
		$text = '"data":[';
        while ($row_rent = $result_rent->fetch_assoc()) {
			if ($row_rent['Total'] != 0 && $row_rent['Startdate'] != '') {
                $row_rent['Day'] = abs((strtotime(date("Y-m-d",time()))-strtotime($row_rent['Startdate']))/86400) + 1;//计算当前日期下租车天数
            }
            $text .= '{"account":"'.$row_rent['Account'].'","number":"' . $row_car['Number'] . '","startdate":"' . $row_car['Startdate'] . '","total":"'.$row_rent['Total'].'"},';
        }
        $text = substr_replace($text, "", strlen($text)) . ']}}';
        echo $text;
    }
	else {
		echo 'failed';
	}
}

function getAllRentCars($connection) {
	$q = 'select * from rentinfo';
	if ($result_rent = mysqli_query($connection, $q)) {
		$text = '{"data":[';
        while ($row_rent = $result_rent->fetch_assoc()) {
			if ($row_rent['Total'] != 0 && $row_rent['Startdate'] != '') {
                $row_rent['Day'] = abs((strtotime(date("Y-m-d",time()))-strtotime($row_rent['Startdate']))/86400) + 1;//计算当前日期下租车天数
            }
            $text .= '{"account":"'.$row_rent['Account'].'","number":"' . $row_rent['Number'] . '","startdate":"' . $row_rent['Startdate'] . '","day":"'.$row_rent['Day'].'","total":"'.$row_rent['Total'].'"},';
        }
        $text = substr_replace($text, "", strlen($text)) . ']}';
        echo $text;
    }
	else {
		echo 'failed';
	}
}

if($_GET['info'] == 'iCON') {
	echo '<br><h3>iCON小组,版权所有</h3><br><img src="ts.png"><br>';
}

if ($con) {
    $query_use_database = 'use zjwdb_6072468';
    $create_table_carsinfo = 'create table carsinfo (Number CHAR (10) PRIMARY KEY,Able SMALLINT NOT NULL,Style CHAR (8) NOT NULL,Brand CHAR (10),Name CHAR (10),People SMALLINT NOT NULL,Cargo SMALLINT  NOT NULL,Price SMALLINT NOT NULL,Deposit SMALLINT NOT NULL,Distance INT NOT NULL,Picture CHAR (100),Note CHAR (100))';
    $create_table_usersinfo = 'create table usersinfo (Account CHAR (16) PRIMARY KEY,Password CHAR (16) NOT NULL,Admin SMALLINT NOT NULL,Name CHAR (10) NOT NULL,Cash INT NOT NULL)';
	$create_table_rentinfo = 'create table rentinfo (Account CHAR (16),Number CHAR (10),Startdate CHAR (12),Day SMALLINT,Total SMALLINT NOT NULL,PRIMARY KEY (Account, Number),FOREIGN KEY (Number) REFERENCES carsinfo(Number),FOREIGN KEY (Account) REFERENCES usersinfo(Account))';
    mysqli_query($con, $query_use_database); 
    if ($_GET['initdatabase'] == 'true') {
        if (mysqli_query($con, $create_table_carsinfo) && mysqli_query($con, $create_table_usersinfo) && mysqli_query($con, $create_table_rentinfo)) {
            echo 'success';
        }else{
            die(mysqli_error($con).'<br>');
        }
    }
    if ($_GET['admin'] == 'true') {
        if ($_GET['insertdata'] == '1') {   //insertAdmin
            if (!insertUsersInfo($con, $_GET['n_account'], $_GET['n_password'], 1, $_GET['n_username']))
                die("<br>创建管理员用户失败<br>");
			else
				echo 'successful';
        }
        if ($_GET['insertdata'] == '3') { //insertCar
            if (!insertCarsInfo($con, $_GET['n_number'], 1, $_GET['n_style'], $_GET['brand'], $_GET['carname'], $_GET['n_people'], $_GET['n_cargo'], $_GET['n_price'], $_GET['deposit'], $_GET['distance'], $_GET['note'] = NULL, $_GET['picture'] = NULL))
                die(mysqli_error($con) . "<br>创建汽车信息失败<br>");
			else
				echo 'successful';
        }
        if ($_GET['deletedata'] == '1') {    //deleteUser
            if (!deleteInfo_common($con, 'usersinfo', 'Account', "'".$_GET['n_account']."'", 'false'))
                die(mysqli_error($con));
			else
				echo 'successful';
        }
        if ($_GET['deletedata'] == '2') {    //deleteCar
            if (!deleteInfo_common($con, 'carsinfo', 'Number', $_GET['n_number'], 'false'))
                die(mysqli_error($con));
			else
				echo 'successful';
        }
        if ($_GET['update'] == '1') {
            updateInfo_common_single($con, 'carsinfo', 'Number', $_GET['n_number'], $_GET['field'], $_GET['newdata']);
			echo 'successful';
        }
		//add start
        if ($_GET['update'] == '3') {
            if (updateUserInfo($con, $_GET['account'], $_GET['n_password'], $_GET['n_admin'], $_GET['n_name']))
                echo 'successful';
            else
                echo 'failed';
        }
        if ($_GET['update'] == '4') {
            if (updateCarInfo($con, $_GET['number'], $_GET['able'], $_GET['style'], $_GET['brand'], $_GET['cname'], $_GET['people'], $_GET['cargo'], $_GET['price'], $_GET['deposit'], $_GET['distance'], $_GET['note'], $_GET['picture']))
                echo 'successful';
            else
                die(mysqli_error($con));
        }
        //add end
        if ($_GET['getinfomode'] == '1') {  //getCarFromUser
            if ($result_user = mysqli_query($con, 'select * from usersinfo where Account = "' . $_GET['account'] . '"')) {
                $row_user = $result_user->fetch_assoc();
                if ($row_user['Total'] != 0 && $result_car = mysqli_query($con, 'select * from carsinfo where Number = "' . $row_user['Number'] . '"')) {
                    $row_car = $result_car->fetch_assoc();
                    echo '{"info":"","data":{"userinfo":"","carinfo":[{"number":"' . $row_car['Number'] . '","able":"' . $row_car['Able'] . '","style":"' . $row_car['Style'] . '","brand":"' . $row_car['Brand'] . '","name":"' . $row_car['Name'] . '","people":"' . $row_car['People'] . '","cargo":"' . $row_car['Cargo'] . '","price":"' . $row_car['Price'] . '"}]}}';
                } else {
                    echo '{"info":"查询失败","data":""}';
                }
            } else {
				echo 'failed';
                die(mysqli_error($con));
            }
        }
		/*
        if ($_GET['getinfomode'] == '4') {   //getHadRentUsers&CarsInfo
            $text = '{"info":"","data":{';
            if ($result_user = mysqli_query($con, 'select * from usersinfo')) {
                while ($row_user = $result_user->fetch_assoc()) {
                    if ($result_car = mysqli_query($con, 'select * from carsinfo')) {
                        while ($row_car = $result_car->fetch_assoc()) {
                            $text .= '{"userinfo":{"account":"' . $row_user['Account'] . '","admin":"' . $row_user['Admin'] . '","name":"' . $row_user['Name'] . '","startdate":"' . $row_user['Startdate'] . '","day":"' . $row_user['Day'] . '","total":"' . $row_user['Total'] . '"},"carinfo":{"number":"' . $row_car['Number'] . '","able":"' . $row_car['Able'] . '","style":"' . $row_car['Style'] . '","brand":"' . $row_car['Brand'] . '","name":"' . $row_car['Name'] . '","people":"' . $row_car['People'] . '","cargo":"' . $row_car['Cargo'] . '","price":"' . $row_car['Price'] . '"}},';
                        }
                    }
                }
            } else {
				echo 'failed';
                die($con);
            }
            $text = substr_replace($text, "", strlen($text)) . ']}}';
            echo $text;
        }
		*/
        if ($_GET['getinfomode'] == '5') {  //getAllUsersInfo
            $text = '{"info":"","data":{"userinfo":[';
            if ($result_user = mysqli_query($con, 'select * from usersinfo')) {
                while ($row_user = $result_user->fetch_assoc()) {
                    $text .= '{"account":"' . $row_user['Account'] . '", "password":"'. $row_user['Password'] .'", "admin":"' . $row_user['Admin'] . '","name":"' . $row_user['Name'] . '","cash":"'.$row_user['Cash'].'"},';
                }
			$text = substr_replace($text, "", strlen($text)) . '],"carinfo":""}}';
                echo $text;
            } else {
				die(mysqli_error($con));
			}
        }
        if ($_GET['getinfomode'] == '6') { //getAllCarsInfo
            $text = '{"info":"","data":{"userinfo":"","carinfo":[';
            if ($result_car = mysqli_query($con, 'select * from carsinfo')) {
                while ($row_car = $result_car->fetch_assoc()) {
                    $text .= '{"number":"' . $row_car['Number'] . '","able":"' . $row_car['Able'] . '","style":"' . $row_car['Style'] . '","brand":"' . $row_car['Brand'] . '","name":"' . $row_car['Name'] . '","people":"' . $row_car['People'] . '","cargo":"' . $row_car['Cargo'] . '","price":"' . $row_car['Price'] . '","deposit":"'.$row_car['Deposit'].'","distance":"'.$row_car['Distance'].'","picture":"'.$row_car['Picture'].'","note":"'.$row_car['Note'].'"},';
                }
                $text = substr_replace($text, "", strlen($text)) . ']}}';
                echo $text;
            } else {
				echo 'failed';
			}
        }
    }
    if ($_GET['insertdata'] == '2') {    //insertUser
        if (!insertUsersInfo($con, $_GET['n_account'], $_GET['n_password'], 0, $_GET['n_username']))
            die("<br>创建用户失败<br>");
		else
			echo 'successful';
    }
	if ($_GET['update'] == '2') {
            if (updateInfo_common_single($con, 'usersinfo', 'Account', "'".$_GET['n_account']."'", $_GET['field'], $_GET['newdata']))
                echo 'successful';
            else
                die(mysqli_error($con));
    }
	if ($_GET['update'] == '5') {
		$query_update = 'update usersinfo set '.$_GET['field'].'="'.$_GET['newdata'].'" where Account="'.$_GET['account'].'"';
            if (mysqli_query($con, $query_update))
                echo 'successful';
            else
                die(mysqli_error($con));
    }
    if ($_GET['getinfomode'] == '3') {   //getUserInfo
        if ($result_user = mysqli_query($con, 'select * from usersinfo where Account = "' . $_GET['account'] . '"')) {
            $row_user = $result_user->fetch_assoc();
			if ($row_user['Total'] != 0 && $row_user['Startdate'] != '') {
                $row_user['Day'] = abs((strtotime(date("Y-m-d",time()))-strtotime($row_user['Startdate']))/86400) + 1;//计算当前日期下租车天数
            }
            echo '{"info":"","data":{"userinfo":[{"account":"' . $row_user['Account'] . '", "password":"'. $row_user['Password'] .'", "admin":"' . $row_user['Admin'] . '","name":"' . $row_user['Name'] . '", "number":"'. $row_user['Number'] .'", "startdate":"' . $row_user['Startdate'] . '","day":"' . $row_user['Day'] . '","total":"' . $row_user['Total'] . '", "cash":"' .$row_user['Cash']. '"}]}, "carinfo":""}';
        }else {
            die('{"info":"查询失败","data":""}');
        }
    }
    if ($_GET['getinfomode'] == '2') {   //getCarFromNumber
        if ($result_car = mysqli_query($con, 'select * from carsinfo where Number = "' . $_GET['number'] . '"')) {
            $row_car = $result_car->fetch_assoc();
            echo '{"info":"","data":{"userinfo":"","carinfo":[{"number":"'.$row_car['Number'].'","able":"'.$row_car['Able'].'","style":"'.$row_car['Style'].'","brand":"'.$row_car['Brand'].'","name":"'.$row_car['Name'].'","people":"'.$row_car['People'].'","cargo":"'.$row_car['Cargo'].'","price":"'.$row_car['Price'].'","deposit":"'.$row_car['Deposit'].'","distance":"'.$row_car['Distance'].'","picture":"'.$row_car['Picture'].'","note":"'.$row_car['Note'].'"}]}}';
        } else {
            echo '{"info":"查询失败","data":""}';
        }
    }
    if ($_GET['searchtable'] == 'usersinfo') {
        if(searchInfo($con, $_GET['searchtable'], $_GET['field'], $_GET['record'], $_GET['symbol']))
			echo 'successful';
    }
    if ($_GET['searchtable'] == 'carsinfo') {
        if(searchInfo($con, $_GET['searchtable'], $_GET['field'], $_GET['record'], $_GET['symbol']))
			echo 'successful';
    }
	if ($_GET['deal'] == '1') {
        if (rentCar($con, $_GET['account'], $_GET['number'], date('y-m-d', time()), $_GET['total']))
            echo 'successful';
        else
            echo 'failed';
    }
    if ($_GET['deal'] == '2') {
        if (backCar($con, $_GET['account'], $_GET['number']))
            echo 'successful';
        else
            echo 'failed';
    }
	if ($_GET['getinfomode'] == '7') {	//获取用户已租车信息
		if (!getRentInfosFromUser($con, $_GET['account'])) {
			die(mysqli_error($con));
		}
	}
	if ($_GET['getinfomode'] == '8') {	//获取所有用户已租车信息
		if (!getAllRentCars($con)) {
			die(mysqli_error($con));
		}
	}
}else {
    echo "连接失败";
}