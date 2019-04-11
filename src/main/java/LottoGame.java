/**
 * class : LottoGame.class
 *
 * version : 1.0.0
 *
 * date : 2019.04.11
 *
 * author : icarus8050
 */

import Util.LottoService;
import domain.Lotto;
import domain.Rank;
import domain.WinningLotto;

import java.util.List;
import java.util.Scanner;

/**
 * 게임의 진행을 담당하는 객체
 */
public class LottoGame {
    private final int LOTTO_PRICE = 1_000;
    private Scanner scanner;
    private List<Lotto> userLottos; //유저가 구매한 로또 리스트
    private Lotto lastWinninNumber; //지난 주 당첨 번호
    private Integer bonusNumber;    //보너스 번호
    private WinningLotto winningLotto;  //당첨 로또
    private List<Rank> rankList;    //유저가 구매한 로또의 당첨 정보

    public LottoGame() {
        scanner = new Scanner(System.in);
    }

    public void run() {
        userLottos = getOrder();
        printUserLottos(userLottos);
        lastWinninNumber = createLastWinningLotto();
        bonusNumber = createBonusNumber();
        winningLotto = LottoService.getWinningLotto(lastWinninNumber, bonusNumber);
        rankList = LottoService.getMatchOfLotto(winningLotto, userLottos);
        printStatistic();
    }

    private List<Lotto> getOrder() {
        List<Lotto> lottoList;

        do {
            System.out.println("구입금액을 입력해 주세요.");
            lottoList = LottoService.getOrder(scanner.nextLine());
        } while (lottoList == null);
        System.out.println();
        return lottoList;
    }

    private void printUserLottos(List<Lotto> userLottos) {
        System.out.println(userLottos.size() + "개를 구매했습니다.");
        for (Lotto lotto : userLottos) {
            System.out.println(lotto.getNumbers());
        }
        System.out.println();
    }

    private Lotto createLastWinningLotto() {
        Lotto lotto;

        do {
            System.out.println("지난 주 당첨 번호를 입력해주세요.");
            lotto = LottoService.getLotto(scanner.nextLine());
        } while (lotto == null);
        return lotto;
    }

    private Integer createBonusNumber() {
        Integer number;

        do {
            System.out.println("보너스 볼을 입력해주세요.");
            number = LottoService.getBonusNumber(scanner.nextLine(), lastWinninNumber);
        } while (number == null);
        System.out.println();
        return number;
    }

    private void printStatistic() {
        int sum = 0;
        double earningsRate;

        System.out.println("당첨 통계");
        System.out.println("-----------");
        for (Rank rank : Rank.values()) {
            sum += countRank(rank);
        }
        earningsRate = (double) sum / (userLottos.size() * LOTTO_PRICE);
        System.out.printf("총 수익률은 %.3f 입니다.", earningsRate);
    }

    private int countRank(Rank targetRank) {
        if (targetRank.equals(Rank.MISS)) {
            return 0;
        }

        int count = (int) rankList.stream().filter(rank -> rank.equals(targetRank)).count();
        System.out.println(targetRank.getCountOfMatch() + "개 일치 (" + targetRank.getWinningMoney() + ")-" + count + "개");
        return targetRank.getWinningMoney() * count;
    }

    public void closeGame() {
        scanner.close();
    }
}
