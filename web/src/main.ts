import './style.css';
import { convertText } from './converter';
import { loadDictionary } from './dictionary';
import { copyText } from './clipboard';

const dictionary = loadDictionary();

const textAreaFrom = document.querySelector<HTMLTextAreaElement>('#textAreaFrom')!;
const textAreaTo = document.querySelector<HTMLTextAreaElement>('#textAreaTo')!;
const buttonConvert = document.querySelector<HTMLButtonElement>('#buttonConvert')!;
const buttonClean = document.querySelector<HTMLButtonElement>('#buttonClean')!;
const notification = document.querySelector<HTMLDivElement>('#notification')!;

let notificationTimer: ReturnType<typeof setTimeout> | undefined;

function showNotification(message: string, isError = false): void {
  notification.textContent = message;
  notification.classList.toggle('error', isError);
  notification.hidden = false;
  if (notificationTimer !== undefined) clearTimeout(notificationTimer);
  notificationTimer = setTimeout(() => {
    notification.hidden = true;
  }, 3000);
}

buttonConvert.addEventListener('click', () => {
  const textFrom = textAreaFrom.value;
  if (textFrom.trim() === '') {
    showNotification('Sem texto para converter!', true);
    return;
  }
  const textConverted = convertText(textFrom, dictionary);
  textAreaTo.value = textConverted;
  textAreaTo.focus();
  textAreaTo.select();
  copyText(textConverted).then((status) => {
    if (status === 'copied') {
      showNotification('Copiado para a área de transferência');
    } else {
      showNotification('Não foi possível copiar automaticamente');
    }
  });
});

buttonClean.addEventListener('click', () => {
  textAreaFrom.value = '';
  textAreaTo.value = '';
});
